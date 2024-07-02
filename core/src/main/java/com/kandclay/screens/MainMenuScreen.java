package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.*;
import com.esotericsoftware.spine.*;
import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.managers.ScreenManager;
import com.kandclay.utils.Constants;
import com.kandclay.utils.ScreenType;
import com.kandclay.utils.TrailDot;

import java.util.HashMap;

public class MainMenuScreen extends BaseScreen {

    private TextureRegion backgroundTexture;
    private boolean isInitialAnimationFinished = false;
    private Viewport backgroundViewport;

    private Viewport minimapViewport;
    private TextureRegion minimapRegion;
    private Stage minimapStage;

    private boolean minimap = true;

    private enum AnimationType {
        MENU
    }

    public MainMenuScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        super(spineAnimationHandler, screenManager);
    }

    @Override
    public void show() {
        super.show();

        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);

        initializeAnimations();

        Texture texture = assetManager.get(Constants.Background.PATH, Texture.class);
        backgroundTexture = new TextureRegion(texture);
        backgroundViewport = new ExtendViewport(Constants.General.WIDTH, Constants.General.HEIGHT);

        if (minimap)
            setUpMinimap();

        shapeRenderer = new ShapeRenderer();

        hoverStates = new HashMap<String, Boolean>();
        hoverStates.put(Constants.MainMenuScreen.BUTTON_PLAY_NAME, false);
        hoverStates.put(Constants.MainMenuScreen.BUTTON_QUIT_NAME, false);
        hoverStates.put(Constants.MainMenuScreen.BUTTON_SETTINGS_NAME, false);

        getStage().addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (isInitialAnimationFinished) {
                    handleHover(x, y);
                }
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleClick(x, y);
                return true;
            }
        });

        minimapStage = new Stage(minimapViewport);
        addClickStage(minimapStage);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(getStage());
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void setUpMinimap() {
        Texture texture = new Texture(Gdx.files.internal("vp/texture.png"));
        minimapRegion = new TextureRegion(texture);

        minimapViewport = new FitViewport(minimapRegion.getRegionWidth(), minimapRegion.getRegionHeight());
    }

    private void initializeAnimations() {
        String atlasPath = Constants.MainMenuScreen.ATLAS;
        String skeletonPath = Constants.MainMenuScreen.JSON;

        skeletons.insert(AnimationType.MENU.ordinal(), spineAnimationHandler.createSkeleton(atlasPath, skeletonPath));
        states.insert(AnimationType.MENU.ordinal(), spineAnimationHandler.createAnimationState(skeletons.get(AnimationType.MENU.ordinal())));

        setSkeletonScale(skeletons.get(AnimationType.MENU.ordinal()), Constants.MainMenuScreen.SKEL_WIDTH_PERCENTAGE, Constants.MainMenuScreen.SKEL_HEIGHT_PERCENTAGE); // Adjust the percentages as needed
        setSkeletonPosition(skeletons.get(AnimationType.MENU.ordinal()), getViewport().getWorldWidth() / 2, getViewport().getWorldHeight() / 2);
        states.get(AnimationType.MENU.ordinal()).setAnimation(0, "animation", false);

        states.get(AnimationType.MENU.ordinal()).addListener(new AnimationState.AnimationStateListener() {
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
                if (entry.getAnimation().getName().equals("animation")) {
                    isInitialAnimationFinished = true;
                }
            }

            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
            }
        });
    }

    private void handleHover(float x, float y) {
        updateHoverState(x, y, Constants.MainMenuScreen.BUTTON_PLAY_NAME, AnimationType.MENU.ordinal(), 1, "Buttons/PlayHoverIn", "Buttons/PlayHoverOut");
        updateHoverState(x, y, Constants.MainMenuScreen.BUTTON_QUIT_NAME, AnimationType.MENU.ordinal(), 2, "Buttons/QuitHoverIn", "Buttons/QuitHoverOut");
        updateHoverState(x, y, Constants.MainMenuScreen.BUTTON_SETTINGS_NAME, AnimationType.MENU.ordinal(), 3, "Buttons/SettingsHoverIn", "Buttons/SettingsHoverOut");
    }

    private void handleClick(float x, float y) {
        if (isHoveringButton(x, y, Constants.MainMenuScreen.BUTTON_PLAY_NAME, AnimationType.MENU.ordinal())) {
            playButtonPressAnimation("Buttons/PlayPress", ScreenType.MAIN);
        } else if (isHoveringButton(x, y, Constants.MainMenuScreen.BUTTON_QUIT_NAME, AnimationType.MENU.ordinal())) {
            playButtonPressAnimation("Buttons/QuitPress", null);
        } else if (isHoveringButton(x, y, Constants.MainMenuScreen.BUTTON_SETTINGS_NAME, AnimationType.MENU.ordinal())) {
            playButtonPressAnimation("Buttons/SettingsPress", ScreenType.OPTIONS);
        }
    }

    private void playButtonPressAnimation(final String animationName, final ScreenType screenType) {
        Gdx.app.log("MainMenuScreen", "Playing button press animation: " + animationName);
        states.get(AnimationType.MENU.ordinal()).setAnimation(4, animationName, false).setListener(new AnimationState.AnimationStateListener() {
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
                Gdx.app.log("MainMenuScreen", "Animation complete: " + animationName);
                if (screenType != null) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            Gdx.app.log("MainMenuScreen", "Changing screen to: " + screenType);
                            screenManager.setScreen(screenType);
                        }
                    });
                } else {
                    Gdx.app.exit();
                }
            }

            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
            }
        });
    }

    @Override
    public void render(float delta) {
        clearScreen();

        states.get(AnimationType.MENU.ordinal()).update(delta);
        states.get(AnimationType.MENU.ordinal()).apply(skeletons.get(AnimationType.MENU.ordinal()));
        skeletons.get(AnimationType.MENU.ordinal()).updateWorldTransform();

        // Render background
        backgroundViewport.apply();
        getBatch().setProjectionMatrix(backgroundViewport.getCamera().combined);
        getBatch().begin();
        getBatch().draw(backgroundTexture, 0, 0, backgroundViewport.getWorldWidth(), backgroundViewport.getWorldHeight());
        getBatch().end();

        // Render content
        getViewport().apply();
        getBatch().setProjectionMatrix(getViewport().getCamera().combined);
        getBatch().begin();
        renderer.draw(getBatch(), skeletons.get(AnimationType.MENU.ordinal()));
        getBatch().end();

        getStage().act(delta);
        getStage().draw();

        TrailDot.renderTrail(delta, getBatch(), getViewport());

        if (minimap) {
            renderMinimap();
            TrailDot.renderTrail(delta, getBatch(), minimapViewport);
        }
    }

    private void renderMinimap() {
        minimapViewport.apply();
        getBatch().setProjectionMatrix(minimapViewport.getCamera().combined);
        getBatch().begin();
        getBatch().draw(minimapRegion, 0, 0);
        getBatch().end();
    }

    private void renderDebug() {
        shapeRenderer.setProjectionMatrix(getViewport().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        drawDebugBounds(Constants.MainMenuScreen.BUTTON_PLAY_NAME);
        drawDebugBounds(Constants.MainMenuScreen.BUTTON_QUIT_NAME);
        drawDebugBounds(Constants.MainMenuScreen.BUTTON_SETTINGS_NAME);
        shapeRenderer.end();
    }

    private void drawDebugBounds(String buttonName) {
        Rectangle bounds = getButtonBounds(buttonName, AnimationType.MENU.ordinal());
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        backgroundViewport.update(width, height, true);

        if (minimap)
            updateMinimapViewport(width, height);

        setSkeletonScale(skeletons.get(AnimationType.MENU.ordinal()), Constants.MainMenuScreen.SKEL_WIDTH_PERCENTAGE, Constants.MainMenuScreen.SKEL_HEIGHT_PERCENTAGE);
        setSkeletonPosition(skeletons.get(AnimationType.MENU.ordinal()), getViewport().getWorldWidth() / 2, getViewport().getWorldHeight() / 2);
    }

    public void updateMinimapViewport(int width, int height) {
        minimapViewport.update(width, height, true);
        // 0,0 in bottom left corner
        minimapViewport.setScreenBounds(width - 200 - 20, height - 200 - 20, 200, 200);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
