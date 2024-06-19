package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.spine.*;

import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.utils.Constants;
import com.kandclay.managers.*;
import com.kandclay.utils.ScreenType;

import java.util.HashMap;

public class MainAnimationScreen extends BaseScreen {
    private SkeletonRenderer renderer;
    private SpineAnimationHandler spineAnimationHandler;
    private Skeleton coinSkeleton;
    private AnimationState coinState;
    private Skeleton buttonSkeleton;
    private AnimationState buttonState;
    private boolean isYellowCoin;
    private TextButton backButton;
    private Slider slider;
    private TextButton modeButton;
    private TextButton changeColorButton;
    private boolean isLooping = true;
    private float speedMultiplier = 1f;
    private float lastSliderValue = 0f;
    private HashMap<String, Boolean> hoverStates;
    private ShapeRenderer shapeRenderer;

    public MainAnimationScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        super(spineAnimationHandler, screenManager);
        this.spineAnimationHandler = spineAnimationHandler;
    }

    @Override
    public void show() {
        super.show();

        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);

        shapeRenderer = new ShapeRenderer();

        isYellowCoin = configManager.getPreference("coinColor", true);
        initializeCoinAnimations(0f);
        initializeButtonAnimations();

        Skin skin = assetManager.get(Constants.Skin.JSON, Skin.class);

        backButton = new TextButton("Back to Menu", skin, Constants.Font.BUTTON);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenManager.setScreen(ScreenType.MENU);
            }
        });

        slider = new Slider(0, 1, 0.01f, false, skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!isLooping) {
                    float progress = slider.getValue();
                    float animationDuration = coinState.getCurrent(0).getAnimation().getDuration();
                    coinState.getCurrent(0).setTrackTime(progress * animationDuration);
                    if (progress != lastSliderValue) {
                        lastSliderValue = progress;
                        System.out.println("Slider changed: " + slider.getValue() + " Mode: Manual");
                    }
                }
            }
        });

        modeButton = new TextButton("Switch to Manual Mode", skin, Constants.Font.BUTTON);
        modeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isLooping = !isLooping;
                System.out.println("Mode changed to: " + (isLooping ? "Automatic" : "Manual"));
                if (isLooping) {
                    modeButton.setText("Switch to Manual Mode");
                    coinState.setAnimation(0, "animation", true);
                } else {
                    modeButton.setText("Switch to Automatic Mode");
                }
            }
        });

        changeColorButton = new TextButton("Change Coin Color", skin, Constants.Font.BUTTON);
        changeColorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleCoinColor();
            }
        });

        Table bottomTable = new Table();
        bottomTable.setFillParent(true);
        bottomTable.bottom();
        bottomTable.add(slider).width(Constants.Buttons.SLIDER_WIDTH).padBottom(Constants.Buttons.PADDING);
        bottomTable.row();
        bottomTable.add(modeButton).padBottom(Constants.Buttons.PADDING);

        Table backButtonTable = new Table();
        backButtonTable.setFillParent(true);
        backButtonTable.bottom().left();
        backButtonTable.add(backButton).width(Constants.Buttons.BACK_BUTTON_WIDTH).height(Constants.Buttons.CONTROL_BUTTON_HEIGHT).pad(Constants.Buttons.PADDING);

        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.top();
        topTable.add(changeColorButton).pad(Constants.Buttons.PADDING);

        stage.addActor(bottomTable);
        stage.addActor(backButtonTable);
        stage.addActor(topTable);

        hoverStates = new HashMap<String, Boolean>();
        hoverStates.put(Constants.MainAnimation.BUTTON_1X, false);
        hoverStates.put(Constants.MainAnimation.BUTTON_2X, false);
        hoverStates.put(Constants.MainAnimation.BUTTON_3X, false);

        stage.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                handleHover(x, y);
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleClick(x, y);
                return true;
            }
        });

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    private void toggleCoinColor() {
        float currentStateTime = coinState.getCurrent(0).getTrackTime();
        isYellowCoin = !isYellowCoin;
        configManager.setPreference("coinColor", isYellowCoin);
        initializeCoinAnimations(currentStateTime);
    }

    private void initializeCoinAnimations(float stateTime) {
        String atlasPath = isYellowCoin ? Constants.Coin.Yellow.ATLAS : Constants.Coin.Red.ATLAS;
        String skeletonPath = isYellowCoin ? Constants.Coin.Yellow.JSON : Constants.Coin.Red.JSON;
        coinSkeleton = spineAnimationHandler.createSkeleton(atlasPath, skeletonPath);
        coinState = spineAnimationHandler.createAnimationState(coinSkeleton);

        setSkeletonScale(coinSkeleton, Constants.MainAnimationScreen.COIN_WIDTH_PERCENTAGE,  Constants.MainAnimationScreen.COIN_HEIGHT_PERCENTAGE);  // Adjust the percentages as needed
        setSkeletonPosition(coinSkeleton, viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);

        coinState.setAnimation(0, "animation", true);
        coinState.getCurrent(0).setTrackTime(stateTime);
        coinState.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void start(AnimationState.TrackEntry entry) {
                System.out.println("Animation started");
            }

            @Override
            public void complete(AnimationState.TrackEntry entry) {
                System.out.println("Animation completed");
            }
        });
    }

    private void initializeButtonAnimations() {
        String atlasPath = Constants.MainAnimation.ATLAS;
        String skeletonPath = Constants.MainAnimation.JSON;

        buttonSkeleton = spineAnimationHandler.createSkeleton(atlasPath, skeletonPath);
        buttonState = spineAnimationHandler.createAnimationState(buttonSkeleton);

        setSkeletonScale(buttonSkeleton, Constants.MainAnimationScreen.BUTTONS_WIDTH_PERCENTAGE,  Constants.MainAnimationScreen.BUTTONS_HEIGHT_PERCENTAGE);  // Adjust the percentages as needed
        setSkeletonPosition(buttonSkeleton, 0, viewport.getWorldHeight());

        playButtonPressAnimation(Constants.MainAnimation.BUTTON_1X, "1x/pressed", 1f);
    }

    private void handleHover(float x, float y) {
        updateHoverState(x, y, Constants.MainAnimation.BUTTON_1X, 1, "1x/hoverIn", "1x/hoverOut");
        updateHoverState(x, y, Constants.MainAnimation.BUTTON_2X, 2, "2x/hoverIn", "2x/hoverOut");
        updateHoverState(x, y, Constants.MainAnimation.BUTTON_3X, 3, "3x/hoverIn", "3x/hoverOut");
    }

    private void updateHoverState(float x, float y, String buttonName, int trackIndex, String hoverInAnim, String hoverOutAnim) {
        boolean isHovered = isHoveringButton(x, y, buttonName);
        boolean wasHovered = hoverStates.get(buttonName);

        if (isHovered && !wasHovered) {
            buttonState.setAnimation(trackIndex, hoverInAnim, false);
        } else if (!isHovered && wasHovered) {
            buttonState.setAnimation(trackIndex, hoverOutAnim, false);
        }

        hoverStates.put(buttonName, isHovered);
    }

    private void handleClick(float x, float y) {
        if (isHoveringButton(x, y, Constants.MainAnimation.BUTTON_1X)) {
            playButtonPressAnimation(Constants.MainAnimation.BUTTON_1X, "1x/pressed", 1f);
        } else if (isHoveringButton(x, y, Constants.MainAnimation.BUTTON_2X)) {
            playButtonPressAnimation(Constants.MainAnimation.BUTTON_2X, "2x/pressed", 2f);
        } else if (isHoveringButton(x, y, Constants.MainAnimation.BUTTON_3X)) {
            playButtonPressAnimation(Constants.MainAnimation.BUTTON_3X, "3x/pressed", 3f);
        }
    }

    private void playButtonPressAnimation(String buttonName, final String animationName, final float speed) {
        Gdx.app.log("MainAnimationScreen", "Playing button press animation: " + animationName);
        buttonState.setAnimation(4, animationName, false).setListener(new AnimationState.AnimationStateListener() {
            @Override
            public void start(AnimationState.TrackEntry entry) {
            }

            @Override
            public void interrupt(AnimationState.TrackEntry entry) {
            }

            @Override
            public void end(AnimationState.TrackEntry entry) {
            }

            @Override
            public void dispose(AnimationState.TrackEntry entry) {
            }

            @Override
            public void complete(AnimationState.TrackEntry entry) {
                Gdx.app.log("MainAnimationScreen", "Animation complete: " + animationName);
                speedMultiplier = speed;
            }

            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
            }
        });
    }

    private boolean isHoveringButton(float x, float y, String buttonName) {
        Rectangle buttonBounds = getButtonBounds(buttonName);
        return buttonBounds.contains(x, y);
    }

    private Rectangle getButtonBounds(String buttonName) {
        String bgSlotName = "bg-" + buttonName;
        Bone bone = buttonSkeleton.findBone(buttonName);
        if (bone == null) return new Rectangle();

        Slot slot = buttonSkeleton.findSlot(bgSlotName);  // Use the background slot
        if (slot == null || !(slot.getAttachment() instanceof RegionAttachment)) return new Rectangle();

        RegionAttachment attachment = (RegionAttachment) slot.getAttachment();
        if (attachment == null) return new Rectangle();

        float[] vertices = new float[8];
        attachment.computeWorldVertices(slot.getBone(), vertices, 0, 2);

        float minX = vertices[0];
        float minY = vertices[1];
        float maxX = vertices[0];
        float maxY = vertices[1];

        for (int i = 2; i < vertices.length; i += 2) {
            if (vertices[i] < minX) minX = vertices[i];
            if (vertices[i + 1] < minY) minY = vertices[i + 1];
            if (vertices[i] > maxX) maxX = vertices[i];
            if (vertices[i + 1] > maxY) maxY = vertices[i + 1];
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (isLooping) {
            coinState.update(delta * speedMultiplier);
        }

        coinState.apply(coinSkeleton);
        coinSkeleton.updateWorldTransform();

        buttonState.update(delta);
        buttonState.apply(buttonSkeleton);
        buttonSkeleton.updateWorldTransform();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Draw the coin animation
        renderer.draw(batch, coinSkeleton);
        // Draw the button animations
        renderer.draw(batch, buttonSkeleton);
        // Draw the UI
        stage.act(delta);
        stage.draw();
        // Draw the trail
        super.renderTrail(delta);
        batch.end();

        // Render debug bounds
        // renderDebug();
    }

    private void renderDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        drawDebugBounds(Constants.MainAnimation.BUTTON_1X);
        drawDebugBounds(Constants.MainAnimation.BUTTON_2X);
        drawDebugBounds(Constants.MainAnimation.BUTTON_3X);
        shapeRenderer.end();
    }

    private void drawDebugBounds(String buttonName) {
        Rectangle bounds = getButtonBounds(buttonName);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);

        setSkeletonScale(coinSkeleton, Constants.MainAnimationScreen.COIN_WIDTH_PERCENTAGE,  Constants.MainAnimationScreen.COIN_HEIGHT_PERCENTAGE);  // Adjust the percentages as needed
        setSkeletonPosition(coinSkeleton, viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);

        setSkeletonScale(buttonSkeleton, Constants.MainAnimationScreen.BUTTONS_WIDTH_PERCENTAGE,  Constants.MainAnimationScreen.BUTTONS_HEIGHT_PERCENTAGE);  // Adjust the percentages as needed
        setSkeletonPosition(buttonSkeleton, 0, viewport.getWorldHeight());
    }

    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
    }
}

