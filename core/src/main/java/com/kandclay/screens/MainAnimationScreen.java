package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.spine.*;

import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.utils.Constants;
import com.kandclay.managers.*;

public class MainAnimationScreen extends BaseScreen {
    private SkeletonRenderer renderer;
    private SpineAnimationHandler spineAnimationHandler;
    private Skeleton skeleton;
    private AnimationState state;
    private boolean isYellowCoin;
    private TextButton backButton;
    private Slider slider;
    private TextButton modeButton;
    private TextButton changeColorButton;
    private boolean isLooping = true;
    private float speedMultiplier = 1f;
    private float lastSliderValue = 0f;

    public MainAnimationScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        super(spineAnimationHandler, screenManager);
        this.spineAnimationHandler = spineAnimationHandler;
    }

    @Override
    public void show() {
        super.show();

        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);

        isYellowCoin = configManager.getPreference("coinColor", true);
        initializeAnimations(0f);

        Skin skin = assetManager.get(Constants.Skin.JSON, Skin.class);

        backButton = new TextButton("Back to Menu", skin, Constants.Font.BUTTON);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenManager.setScreen(Constants.ScreenType.MENU);
            }
        });

        slider = new Slider(0, 1, 0.01f, false, skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!isLooping) {
                    float progress = slider.getValue();
                    float animationDuration = state.getCurrent(0).getAnimation().getDuration();
                    state.getCurrent(0).setTrackTime(progress * animationDuration);
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
                    state.setAnimation(0, "animation", true);
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

        TextButton speed1xButton = new TextButton("1x", skin, Constants.Font.BUTTON);
        speed1xButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                speedMultiplier = 1f;
                System.out.println("Speed set to 1x");
            }
        });

        TextButton speed2xButton = new TextButton("2x", skin, Constants.Font.BUTTON);
        speed2xButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                speedMultiplier = 2f;
                System.out.println("Speed set to 2x");
            }
        });

        TextButton speed3xButton = new TextButton("3x", skin, Constants.Font.BUTTON);
        speed3xButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                speedMultiplier = 3f;
                System.out.println("Speed set to 3x");
            }
        });

        Table controlTable = new Table();
        controlTable.top().left();
        controlTable.setFillParent(true);
        controlTable.add(speed1xButton).size(Constants.Buttons.BUTTON_WIDTH, Constants.Buttons.BUTTON_HEIGHT).pad(Constants.Buttons.PADDING).row();
        controlTable.add(speed2xButton).size(Constants.Buttons.BUTTON_WIDTH, Constants.Buttons.BUTTON_HEIGHT).pad(Constants.Buttons.PADDING).row();
        controlTable.add(speed3xButton).size(Constants.Buttons.BUTTON_WIDTH, Constants.Buttons.BUTTON_HEIGHT).pad(Constants.Buttons.PADDING).row();

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

        stage.addActor(controlTable);
        stage.addActor(bottomTable);
        stage.addActor(backButtonTable);
        stage.addActor(topTable);

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    private void toggleCoinColor() {
        float currentStateTime = state.getCurrent(0).getTrackTime();
        isYellowCoin = !isYellowCoin;
        configManager.setPreference("coinColor", isYellowCoin);
        initializeAnimations(currentStateTime);
    }

    private void initializeAnimations(float stateTime) {
        String atlasPath = isYellowCoin ? Constants.Coin.Yellow.ATLAS : Constants.Coin.Red.ATLAS;
        String skeletonPath = isYellowCoin ? Constants.Coin.Yellow.JSON : Constants.Coin.Red.JSON;
        skeleton = spineAnimationHandler.createSkeleton(atlasPath, skeletonPath);
        state = spineAnimationHandler.createAnimationState(skeleton);
        skeleton.setScale(1f, 1f);
        setSkeletonPosition();
        state.setAnimation(0, "animation", true);
        state.getCurrent(0).setTrackTime(stateTime);
        state.addListener(new AnimationState.AnimationStateAdapter() {
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

    @Override
    public void render(float delta) {
        super.render(delta);

        if (isLooping) {
            state.update(delta * speedMultiplier);
        }

        state.apply(skeleton);
        skeleton.updateWorldTransform();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Draw the animations
        renderer.draw(batch, skeleton);
        // Draw the UI
        stage.act(delta);
        stage.draw();
        // Draw the trail
        super.renderTrail(delta);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        setSkeletonPosition();
    }

    private void setSkeletonPosition() {
        if (skeleton != null) {
            float centerX = viewport.getWorldWidth() / 2;
            float centerY = viewport.getWorldHeight() / 2;
            skeleton.setPosition(centerX, centerY);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
