package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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

import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.utils.Constants;
import com.kandclay.managers.*;
import com.kandclay.utils.ScreenType;

import java.util.HashMap;

public class MainAnimationScreen extends BaseScreen {
    private boolean isYellowCoin;
    private TextButton backButton;
    private Slider slider;
    private TextButton modeButton;
    private TextButton changeCoinColorButton;
    private boolean isLooping = true;
    private float speedMultiplier = 1f;
    private float lastSliderValue = 0f;
    private Texture backgroundTexture;
    private TextButton swapSkinsButton;  // New button to swap skins

//    private Stage getStage();
//    private Camera getCamera();
//    private Viewport getViewport();
//    private SpriteBatch getBatch();

    private static final int COIN = 0;
    private static final int BUTTON = 1;

    public MainAnimationScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        super(spineAnimationHandler, screenManager);
        this.spineAnimationHandler = spineAnimationHandler;
    }

    @Override
    public void show() {

        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);
        backgroundTexture = assetManager.get(Constants.Background.PATH, Texture.class);

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
                    float animationDuration = states.get(COIN).getCurrent(0).getAnimation().getDuration();
                    states.get(COIN).getCurrent(0).setTrackTime(progress * animationDuration);
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
                    states.get(COIN).setAnimation(0, "animation", true);
                } else {
                    modeButton.setText("Switch to Automatic Mode");
                }
            }
        });

        changeCoinColorButton = new TextButton("Change Coin Color", skin, Constants.Font.BUTTON);
        changeCoinColorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                swapCoinColor();
            }
        });

        swapSkinsButton = new TextButton("Swap Skins", skin, Constants.Font.BUTTON);
        swapSkinsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                swapSkins();
            }
        });

        Table bottomTable = new Table();
        bottomTable.setFillParent(true);
        bottomTable.bottom();
        bottomTable.add(slider).width(Constants.UIButtons.SLIDER_WIDTH).padBottom(Constants.UIButtons.PADDING);
        bottomTable.row();
        bottomTable.add(modeButton).padBottom(Constants.UIButtons.PADDING);

        Table backButtonTable = new Table();
        backButtonTable.setFillParent(true);
        backButtonTable.bottom().left();
        backButtonTable.add(backButton).width(Constants.UIButtons.BACK_BUTTON_WIDTH).height(Constants.UIButtons.CONTROL_BUTTON_HEIGHT).pad(Constants.UIButtons.PADDING);

        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.top();
        topTable.add(changeCoinColorButton).pad(Constants.UIButtons.PADDING);
        topTable.row();
        topTable.add(swapSkinsButton).pad(Constants.UIButtons.PADDING);  // Add the new button to the layout

        getStage().addActor(bottomTable);
        getStage().addActor(backButtonTable);
        getStage().addActor(topTable);

        hoverStates = new HashMap<String, Boolean>();
        hoverStates.put(Constants.MainAnimationScreen.BUTTON_1X_NAME, false);
        hoverStates.put(Constants.MainAnimationScreen.BUTTON_2X_NAME, false);
        hoverStates.put(Constants.MainAnimationScreen.BUTTON_3X__NAME, false);

        getStage().addListener(new InputListener() {
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

        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        getStage().getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(getStage());
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void swapCoinColor() {
        float currentStateTime = states.get(COIN).getCurrent(0).getTrackTime();
        isYellowCoin = !isYellowCoin;
        configManager.setPreference("coinColor", isYellowCoin);
        swapCoinAnimation(currentStateTime);
    }

    private void configureCoinAnimation(float stateTime, boolean isAddOperation) {
        String atlasPath = isYellowCoin ? Constants.MainAnimationScreen.YellowCoin.ATLAS : Constants.MainAnimationScreen.RedCoin.ATLAS;
        String skeletonPath = isYellowCoin ? Constants.MainAnimationScreen.YellowCoin.JSON : Constants.MainAnimationScreen.RedCoin.JSON;

        if (isAddOperation) {
            skeletons.add(COIN, spineAnimationHandler.createSkeleton(atlasPath, skeletonPath));
            states.add(COIN, spineAnimationHandler.createAnimationState(skeletons.get(COIN)));
        } else {
            skeletons.set(COIN, spineAnimationHandler.createSkeleton(atlasPath, skeletonPath));
            states.set(COIN, spineAnimationHandler.createAnimationState(skeletons.get(COIN)));
        }

        setSkeletonScale(skeletons.get(COIN), Constants.MainAnimationScreen.COIN_WIDTH_PERCENTAGE, Constants.MainAnimationScreen.COIN_HEIGHT_PERCENTAGE);
        setSkeletonPosition(skeletons.get(COIN), getViewport().getWorldWidth() / 2, getViewport().getWorldHeight() / 2);

        states.get(COIN).setAnimation(0, "animation", true);
        states.get(COIN).getCurrent(0).setTrackTime(stateTime);
        states.get(COIN).addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                System.out.println("Animation completed");
            }
        });
    }

    private void initializeCoinAnimations(float stateTime) {
        configureCoinAnimation(stateTime, true);
    }

    private void swapCoinAnimation(float stateTime) {
        configureCoinAnimation(stateTime, false);
    }


    private void initializeButtonAnimations() {
        String atlasPath = Constants.MainAnimationScreen.ATLAS;
        String skeletonPath = Constants.MainAnimationScreen.JSON;

        skeletons.add(BUTTON, spineAnimationHandler.createSkeleton(atlasPath, skeletonPath));
        states.add(BUTTON, spineAnimationHandler.createAnimationState(skeletons.get(BUTTON)));

        setSkeletonScale(skeletons.get(BUTTON), Constants.MainAnimationScreen.BUTTONS_WIDTH_PERCENTAGE, Constants.MainAnimationScreen.BUTTONS_HEIGHT_PERCENTAGE);  // Adjust the percentages as needed
        setSkeletonPosition(skeletons.get(BUTTON), 0, getViewport().getWorldHeight());

        playButtonPressAnimation(Constants.MainAnimationScreen.BUTTON_1X_NAME, "1x/pressed", 1f);
    }

    private void handleHover(float x, float y) {
        updateHoverState(x, y, Constants.MainAnimationScreen.BUTTON_1X_NAME, BUTTON, 1, "1x/hoverIn", "1x/hoverOut");
        updateHoverState(x, y, Constants.MainAnimationScreen.BUTTON_2X_NAME, BUTTON, 2, "2x/hoverIn", "2x/hoverOut");
        updateHoverState(x, y, Constants.MainAnimationScreen.BUTTON_3X__NAME, BUTTON, 3, "3x/hoverIn", "3x/hoverOut");
    }

    private void swapSkins() {
        String currentSkin = skeletons.get(BUTTON).getSkin().getName();
        String newSkin = currentSkin.equals("Saturated") ? "Accessible" : "Saturated";
        skeletons.get(BUTTON).setSkin(newSkin);
    }

    private void handleClick(float x, float y) {
        if (isHoveringButton(x, y, Constants.MainAnimationScreen.BUTTON_1X_NAME, BUTTON)) {
            playButtonPressAnimation(Constants.MainAnimationScreen.BUTTON_1X_NAME, "1x/pressed", 1f);
        } else if (isHoveringButton(x, y, Constants.MainAnimationScreen.BUTTON_2X_NAME, BUTTON)) {
            playButtonPressAnimation(Constants.MainAnimationScreen.BUTTON_2X_NAME, "2x/pressed", 2f);
        } else if (isHoveringButton(x, y, Constants.MainAnimationScreen.BUTTON_3X__NAME, BUTTON)) {
            playButtonPressAnimation(Constants.MainAnimationScreen.BUTTON_3X__NAME, "3x/pressed", 3f);
        }
    }

    private void playButtonPressAnimation(String buttonName, final String animationName, final float speed) {
        Gdx.app.log("MainAnimationScreen", "Playing button press animation: " + animationName);
        states.get(BUTTON).setAnimation(4, animationName, false).setListener(new AnimationState.AnimationStateListener() {
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

    @Override
    protected Rectangle getButtonBounds(String buttonName, int pos) {
        String bgSlotName = "bg-" + buttonName;
        return getRectangle(buttonName, bgSlotName, skeletons.get(pos));
    }

    @Override
    public void render(float delta) {
        clearScreen();

        if (isLooping) {
            states.get(COIN).update(delta * speedMultiplier);
        }

        states.get(COIN).apply(skeletons.get(COIN));
        skeletons.get(COIN).updateWorldTransform();

        states.get(BUTTON).update(delta);
        states.get(BUTTON).apply(skeletons.get(BUTTON));
        skeletons.get(BUTTON).updateWorldTransform();

        getViewport().apply();
        getBatch().setProjectionMatrix(getCamera().combined);

        getBatch().begin();
        getBatch().draw(backgroundTexture, 0, 0, getViewport().getWorldWidth(), getViewport().getWorldHeight());
        // Draw the coin animation
        renderer.draw(getBatch(), skeletons.get(COIN));
        // Draw the button animations
        renderer.draw(getBatch(), skeletons.get(BUTTON));
        // Draw the UI
        getStage().act(delta);
        getStage().draw();
        // Draw the trail
        super.renderTrail(delta, getBatch());
        getBatch().end();

        // Render debug bounds
        // renderDebug();
    }

    private void renderDebug() {
        shapeRenderer.setProjectionMatrix(getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        drawDebugBounds(Constants.MainAnimationScreen.BUTTON_1X_NAME);
        drawDebugBounds(Constants.MainAnimationScreen.BUTTON_2X_NAME);
        drawDebugBounds(Constants.MainAnimationScreen.BUTTON_3X__NAME);
        shapeRenderer.end();
    }

    private void drawDebugBounds(String buttonName) {
        Rectangle bounds = getButtonBounds(buttonName, BUTTON);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        getViewport().update(width, height, true);
        getStage().getViewport().update(width, height, true);

        setSkeletonScale(skeletons.get(COIN), Constants.MainAnimationScreen.COIN_WIDTH_PERCENTAGE, Constants.MainAnimationScreen.COIN_HEIGHT_PERCENTAGE);  // Adjust the percentages as needed
        setSkeletonPosition(skeletons.get(COIN), getViewport().getWorldWidth() / 2, getViewport().getWorldHeight() / 2);

        setSkeletonScale(skeletons.get(BUTTON), Constants.MainAnimationScreen.BUTTONS_WIDTH_PERCENTAGE, Constants.MainAnimationScreen.BUTTONS_HEIGHT_PERCENTAGE);  // Adjust the percentages as needed
        setSkeletonPosition(skeletons.get(BUTTON), 0, getViewport().getWorldHeight());
    }

    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
    }
}

