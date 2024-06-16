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
    private OrthographicCamera camera;
    private SkeletonRenderer renderer;
    private SpineAnimationHandler spineAnimationHandler;

    private Skeleton skeleton;
    private AnimationState state;

    private boolean isYellowCoin;
    private TextButton backButton;
    private Slider slider;
    private TextButton modeButton;
    private TextButton changeColorButton;

    private boolean isLooping = true; // Set initial state to automatic (looping)
    private float speedMultiplier = 1f;
    private float lastSliderValue = 0f; // Track last slider value for changes

    public MainAnimationScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        super(spineAnimationHandler, screenManager);
        this.spineAnimationHandler = spineAnimationHandler;
    }

    @Override
    public void show() {
        super.show();

        camera = new OrthographicCamera();
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);

        // Load the coin color preference
        isYellowCoin = configManager.getPreference("coinColor", true);

        initializeAnimations(0f);  // Start with the initial state time

        // Set up the camera
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Skin skin = assetManager.get(Constants.Skin.JSON, Skin.class);

        // Initialize back button
        backButton = new TextButton("Back to Menu", skin,  Constants.Font.BUTTON);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenManager.setScreen(Constants.ScreenType.MENU);
            }
        });

        // Initialize slider
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

        // Initialize mode button
        modeButton = new TextButton("Switch to Manual Mode", skin,  Constants.Font.BUTTON);
        modeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isLooping = !isLooping;
                System.out.println("Mode changed to: " + (isLooping ? "Automatic" : "Manual"));
                if (isLooping) {
                    modeButton.setText("Switch to Manual Mode");
                    state.setAnimation(0, "animation", true);  // Restart the animation loop
                } else {
                    modeButton.setText("Switch to Automatic Mode");
                }
            }
        });

        // Initialize change color button
        changeColorButton = new TextButton("Change Coin Color", skin,  Constants.Font.BUTTON);
        changeColorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleCoinColor();
            }
        });

        // Initialize speed control buttons
        TextButton speed1xButton = new TextButton("1x", skin,  Constants.Font.BUTTON);
        speed1xButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                speedMultiplier = 1f;
                System.out.println("Speed set to 1x");
            }
        });

        TextButton speed2xButton = new TextButton("2x", skin,  Constants.Font.BUTTON);
        speed2xButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                speedMultiplier = 2f;
                System.out.println("Speed set to 2x");
            }
        });

        TextButton speed3xButton = new TextButton("3x", skin,  Constants.Font.BUTTON);
        speed3xButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                speedMultiplier = 3f;
                System.out.println("Speed set to 3x");
            }
        });

        // Create control table for speed buttons
        Table controlTable = new Table();
        controlTable.top().left();
        controlTable.setFillParent(true);
        controlTable.add(speed1xButton).size(Constants.Buttons.BUTTON_WIDTH, Constants.Buttons.BUTTON_HEIGHT).pad(Constants.Buttons.PADDING).row();
        controlTable.add(speed2xButton).size(Constants.Buttons.BUTTON_WIDTH, Constants.Buttons.BUTTON_HEIGHT).pad(Constants.Buttons.PADDING).row();
        controlTable.add(speed3xButton).size(Constants.Buttons.BUTTON_WIDTH, Constants.Buttons.BUTTON_HEIGHT).pad(Constants.Buttons.PADDING).row();

        // Create table for slider and mode button
        Table bottomTable = new Table();
        bottomTable.setFillParent(true);
        bottomTable.bottom();
        bottomTable.add(slider).width(Constants.Buttons.SLIDER_WIDTH).padBottom(Constants.Buttons.PADDING);
        bottomTable.row();
        bottomTable.add(modeButton).padBottom(Constants.Buttons.PADDING);

        // Create table for back button
        Table backButtonTable = new Table();
        backButtonTable.setFillParent(true);
        backButtonTable.bottom().left();
        backButtonTable.add(backButton).width(Constants.Buttons.BACK_BUTTON_WIDTH).height(Constants.Buttons.CONTROL_BUTTON_HEIGHT).pad(Constants.Buttons.PADDING);

        // Create table for change color button
        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.top();
        topTable.add(changeColorButton).pad(Constants.Buttons.PADDING);

        // Add all tables to the stage
        stage.addActor(controlTable);
        stage.addActor(bottomTable);
        stage.addActor(backButtonTable);
        stage.addActor(topTable);
    }

    private void toggleCoinColor() {
        float currentStateTime = state.getCurrent(0).getTrackTime();
        isYellowCoin = !isYellowCoin;
        configManager.setPreference("coinColor", isYellowCoin);
        initializeAnimations(currentStateTime);  // Re-initialize the animations with the new coin color
    }

    private void initializeAnimations(float stateTime) {
        String atlasPath = isYellowCoin ? Constants.Coin.Yellow.ATLAS : Constants.Coin.Red.ATLAS;
        String skeletonPath = isYellowCoin ? Constants.Coin.Yellow.JSON : Constants.Coin.Red.JSON;

        skeleton = spineAnimationHandler.createSkeleton(atlasPath, skeletonPath);
        state = spineAnimationHandler.createAnimationState(skeleton);
        skeleton.setScale(1f, 1f);

        setSkeletonPosition();

        state.setAnimation(0, "animation", true);  // Loop the animation by default

        // Set the state time to resume from the same frame
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

        camera.update();
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
        camera.setToOrtho(false, width, height);
        camera.update();
        setSkeletonPosition();
    }

    private void setSkeletonPosition() {
        // Center the skeleton on the screen
        float centerX = (camera.viewportWidth - skeleton.getData().getWidth()) / 2;
        float centerY = (camera.viewportHeight - skeleton.getData().getHeight()) / 2;
        skeleton.setPosition(centerX, centerY);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
