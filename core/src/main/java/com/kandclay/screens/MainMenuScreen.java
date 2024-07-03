package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.*;
import com.esotericsoftware.spine.*;
import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.managers.MyAssetManager;
import com.kandclay.managers.ScreenManager;
import com.kandclay.utils.Constants;
import com.kandclay.utils.ScreenType;
import com.kandclay.utils.TrailDot;

import java.util.HashMap;

public class MainMenuScreen extends BaseScreen {

    private boolean isInitialAnimationFinished = false;
    private final boolean debugMode = false;

    private Viewport backgroundViewport;

    private SpriteBatch batch;
    private Camera camera;
    private Viewport viewport;
    private Stage stage;

    private Camera minimapCamera;
    private Viewport minimapViewport;
    private Stage minimapStage;

    private TextureRegion backgroundTexture;
    private TextureRegion minimapRegion;

    private enum AnimationType {
        MENU_1, MENU_2
    }

    public MainMenuScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        super(spineAnimationHandler, screenManager);

    }

    @Override
    public void show() {
        initializeCommonComponents();
        initializeBackground();
        initializeMainContent();
        initializeMinimap();
        setupInputProcessing();
    }

    private void initializeCommonComponents() {
        batch = new SpriteBatch();
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        shapeRenderer = new ShapeRenderer();
        initializeHoverStates();
    }

    private void initializeHoverStates() {
        hoverStates = new HashMap<String, Boolean>();
        hoverStates.put(Constants.MainMenuScreen.BUTTON_PLAY_NAME, false);
        hoverStates.put(Constants.MainMenuScreen.BUTTON_QUIT_NAME, false);
        hoverStates.put(Constants.MainMenuScreen.BUTTON_STGS_NAME, false);
    }

    private void initializeBackground() {
        Texture texture = assetManager.get(Constants.Background.PATH, Texture.class);
        backgroundTexture = new TextureRegion(texture);
        backgroundViewport = new ExtendViewport(Constants.General.EMBED_WIDTH, Constants.General.EMBED_HEIGHT);
    }

    private void initializeMainContent() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Constants.General.EMBED_WIDTH, Constants.General.EMBED_HEIGHT, camera);
        stage = new Stage(viewport);
        initializeAnimations();
        setUpStage(stage, viewport, AnimationType.MENU_1.ordinal());
    }

    private void initializeMinimap() {
        Texture texture = assetManager.get(Constants.Minimap.PATH, Texture.class);
        minimapRegion = new TextureRegion(texture);
        minimapCamera = new OrthographicCamera();
        minimapViewport = new FitViewport(minimapRegion.getRegionWidth(), minimapRegion.getRegionHeight(), minimapCamera);
        minimapStage = new Stage(minimapViewport);
        setUpStage(minimapStage, minimapViewport, AnimationType.MENU_2.ordinal());
    }

    private void setupInputProcessing() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        // Since the minimap is on top, it should be processed first
        multiplexer.addProcessor(minimapStage);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void setUpStage(Stage stage, Viewport viewport, final int skeletonIndex) {
        stage.addListener(createStageInputListener(skeletonIndex));
        addTrailToStage(stage, viewport);
    }

    private InputListener createStageInputListener(final int skeletonIndex) {
        return new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (isInitialAnimationFinished) {
                    handleHover(x, y, skeletonIndex);
                }
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleClick(x, y, skeletonIndex);
                return true;
            }
        };
    }

    private void initializeMenuSkeleton(int skeletonIndex) {

        String atlasPath = Constants.MainMenuScreen.ATLAS;
        String skeletonPath = Constants.MainMenuScreen.JSON;

        skeletons.insert(skeletonIndex, spineAnimationHandler.createSkeleton(atlasPath, skeletonPath));
        states.insert(skeletonIndex, spineAnimationHandler.createAnimationState(skeletons.get(skeletonIndex)));

        // updateSkeletons(viewport);
        states.get(skeletonIndex).setAnimation(0, "animation", false);

        states.get(skeletonIndex).addListener(new AnimationState.AnimationStateListener() {
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

    private void renderBackground() {
        backgroundViewport.apply();
        batch.setProjectionMatrix(backgroundViewport.getCamera().combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, backgroundViewport.getWorldWidth(), backgroundViewport.getWorldHeight());
        batch.end();
    }

    private void renderMainContent(float delta) {
        renderMenu(delta, batch, viewport, AnimationType.MENU_1.ordinal());
        renderDebug(viewport, Color.RED, AnimationType.MENU_1.ordinal());
        stage.act(delta);
        stage.draw();
        TrailDot.renderTrail(delta, batch, viewport);
    }

    private void renderMinimap(float delta) {
        minimapViewport.apply();
        batch.setProjectionMatrix(minimapViewport.getCamera().combined);
        batch.begin();
        batch.draw(minimapRegion, 0, 0);
        batch.end();

        renderMenu(delta, batch, minimapViewport, AnimationType.MENU_2.ordinal());
        renderDebug(minimapViewport, Color.GREEN, AnimationType.MENU_2.ordinal());
        minimapStage.act(delta);
        minimapStage.draw();
        TrailDot.renderTrail(delta, batch, minimapViewport);
    }

    private void renderMenu(float delta, SpriteBatch batch, Viewport viewport, int skeletonIndex) {
        Skeleton skeleton = skeletons.get(skeletonIndex);
        AnimationState state = states.get(skeletonIndex);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        state.update(delta);
        state.apply(skeleton);
        skeleton.updateWorldTransform();
        updateSkeletonScaleAndPosition(viewport, skeletonIndex);

        skeletonRenderer.draw(batch, skeleton);

        batch.end();
    }

    private void initializeAnimations() {
        initializeMenuSkeleton(AnimationType.MENU_1.ordinal());
        initializeMenuSkeleton(AnimationType.MENU_2.ordinal());
    }

    private void handleHover(float x, float y, int skeletonIndex) {
        updateHoverState(x, y, Constants.MainMenuScreen.BUTTON_PLAY_NAME, skeletonIndex, 1, "Buttons/PlayHoverIn", "Buttons/PlayHoverOut");
        updateHoverState(x, y, Constants.MainMenuScreen.BUTTON_QUIT_NAME, skeletonIndex, 2, "Buttons/QuitHoverIn", "Buttons/QuitHoverOut");
        updateHoverState(x, y, Constants.MainMenuScreen.BUTTON_STGS_NAME, skeletonIndex, 3, "Buttons/SettingsHoverIn", "Buttons/SettingsHoverOut");
    }

    private void handleClick(float x, float y, int skeletonIndex) {
        if (isHoveringButton(x, y, Constants.MainMenuScreen.BUTTON_PLAY_NAME, skeletonIndex)) {
            playButtonPressAnimation("Buttons/PlayPress", ScreenType.MAIN, skeletonIndex);
        } else if (isHoveringButton(x, y, Constants.MainMenuScreen.BUTTON_QUIT_NAME, skeletonIndex)) {
            playButtonPressAnimation("Buttons/QuitPress", null, skeletonIndex);
        } else if (isHoveringButton(x, y, Constants.MainMenuScreen.BUTTON_STGS_NAME, skeletonIndex)) {
            playButtonPressAnimation("Buttons/SettingsPress", ScreenType.OPTIONS, skeletonIndex);
        }
    }

    private void playButtonPressAnimation(final String animationName, final ScreenType screenType, int skeletonIndex) {
        Gdx.app.log("MainMenuScreen", "Playing button press animation: " + animationName);
        states.get(skeletonIndex).setAnimation(4, animationName, false).setListener(new AnimationState.AnimationStateListener() {
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

        clearScreen();

        renderBackground();

        renderMainContent(delta);

        renderMinimap(delta);
    }

    private void renderDebug(Viewport viewport, Color color, int skeletonIndex) {
        if (debugMode) {
            shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(color);
            drawDebugBounds(Constants.MainMenuScreen.BUTTON_PLAY_NAME, skeletonIndex);
            drawDebugBounds(Constants.MainMenuScreen.BUTTON_QUIT_NAME, skeletonIndex);
            drawDebugBounds(Constants.MainMenuScreen.BUTTON_STGS_NAME, skeletonIndex);
            shapeRenderer.end();
        }
    }

    private void drawDebugBounds(String buttonName, int skeletonIndex) {
        Rectangle bounds = getButtonBounds(buttonName, skeletonIndex);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        backgroundViewport.update(width, height, true);
        updateMinimapViewport(width, height);
    }

    private void updateMinimapViewport(int width, int height) {
        minimapViewport.update(width, height, true);
        minimapViewport.setScreenBounds(width - Constants.Minimap.WIDTH - Constants.Minimap.PADDING,
            height - Constants.Minimap.HEIGHT - Constants.Minimap.PADDING, Constants.Minimap.WIDTH, Constants.Minimap.HEIGHT);
    }

    private void updateSkeletonScaleAndPosition(Viewport viewport, int skeletonIndex) {
        setSkeletonScale(skeletons.get(skeletonIndex), Constants.MainMenuScreen.SKEL_WIDTH_PERCENTAGE, Constants.MainMenuScreen.SKEL_HEIGHT_PERCENTAGE, viewport);
        setSkeletonPosition(skeletons.get(skeletonIndex), viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
    }

    @Override
    public void dispose() {
        stage.dispose();
        minimapStage.dispose();
        if (batch != null) {
            batch.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
