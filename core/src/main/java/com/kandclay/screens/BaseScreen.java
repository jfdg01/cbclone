package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.managers.AudioManager;
import com.kandclay.managers.ConfigurationManager;
import com.kandclay.managers.MyAssetManager;
import com.kandclay.managers.ScreenManager;
import com.kandclay.utils.Constants;

import javax.swing.text.View;
import java.util.Iterator;

public abstract class BaseScreen implements Screen {
    protected MyAssetManager assetManager;
    protected AudioManager audioManager;
    protected SpineAnimationHandler spineAnimationHandler;
    protected ConfigurationManager configManager;
    protected ScreenManager screenManager;
    private SnapshotArray<TrailDot> trailDots;
    private int trailDotCount = 0;
    private SpriteBatch batch;
    private Camera camera;
    private Viewport viewport;
    private Stage stage;

    public BaseScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        this.assetManager = MyAssetManager.getInstance();
        this.audioManager = AudioManager.getInstance();
        this.configManager = ConfigurationManager.getInstance();
        this.spineAnimationHandler = spineAnimationHandler;
        this.screenManager = screenManager;

        // Initialize camera and viewport
        this.viewport = new ExtendViewport(Constants.General.WIDTH, Constants.General.HEIGHT);
        this.camera = viewport.getCamera();

        this.stage = new Stage(viewport);
        this.trailDots = new SnapshotArray<TrailDot>();
        this.batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);

        stage.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                createTrailDot(x, y);
                Gdx.app.log("BaseScreen", "Mouse moved at x=" + x + " y=" + y);
                return true;
            }
        });
    }

    @Override
    public void show() {

    }

    public void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void render(float delta) {
        clearScreen();
    }

    void renderTrail(float delta, SpriteBatch batch) {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        Iterator<TrailDot> iterator = trailDots.iterator();
        while (iterator.hasNext()) {
            TrailDot trailDot = iterator.next();
            trailDot.state.update(delta);
            trailDot.state.apply(trailDot.skeleton);
            trailDot.skeleton.updateWorldTransform();
            trailDot.skeleton.setPosition(trailDot.x, trailDot.y);

            trailDot.renderer.draw(batch, trailDot.skeleton);

            if (trailDot.state.getCurrent(0) == null || trailDot.state.getCurrent(0).isComplete()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        stage.getViewport().update(width, height, true);
        Gdx.app.log("BaseScreen", "Resize: width=" + width + " height=" + height);
        Gdx.app.log("BaseScreen", "Camera position: x=" + camera.position.x + " y=" + camera.position.y);
        Gdx.app.log("BaseScreen", "Viewport: worldWidth=" + viewport.getWorldWidth() + " worldHeight=" + viewport.getWorldHeight() + " screenWidth=" + viewport.getScreenWidth() + " screenHeight=" + viewport.getScreenHeight());
        Gdx.app.log("BaseScreen", "Stage: width=" + stage.getWidth() + " height=" + stage.getHeight());
    }

    @Override
    public void pause() {
        // Override in subclasses if needed
    }

    @Override
    public void resume() {
        // Override in subclasses if needed
    }

    @Override
    public void hide() {
        // Override in subclasses to hide the screen
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (batch != null) {
            batch.dispose();
        }
    }

    private void createTrailDot(float x, float y) {
        float hue = (trailDotCount % Constants.TrailDot.NUMBER_OF_COLORS);

        Color currentColor = new Color();
        currentColor.fromHsv(hue, Constants.TrailDot.SATURATION, Constants.TrailDot.VALUE);
        currentColor.a = Constants.TrailDot.ALPHA;

        String trailAtlasPath = Constants.TrailDot.ATLAS;
        String trailSkeletonPath = Constants.TrailDot.JSON;

        Skeleton trailSkeleton = spineAnimationHandler.createSkeleton(trailAtlasPath, trailSkeletonPath);
        AnimationState trailState = spineAnimationHandler.createAnimationState(trailSkeleton);
        SkeletonRenderer trailRenderer = new SkeletonRenderer();
        trailRenderer.setPremultipliedAlpha(true);

        float randomScale = MathUtils.random(Constants.TrailDot.MIN_SCALE, Constants.TrailDot.MAX_SCALE);
        float randomRotation = MathUtils.random(Constants.TrailDot.MIN_ROTATION, Constants.TrailDot.MAX_ROTATION);

        trailSkeleton.setPosition(x, y);
        trailSkeleton.setColor(currentColor);
        trailSkeleton.setScale(randomScale, randomScale);
        trailSkeleton.getRootBone().setRotation(randomRotation);

        trailState.setAnimation(0, "animation", false);

        trailDots.add(new TrailDot(trailSkeleton, trailState, trailRenderer, x, y));
        trailDotCount++;
    }

    private static class TrailDot {
        public Skeleton skeleton;
        public AnimationState state;
        public SkeletonRenderer renderer;
        public float x, y;

        public TrailDot(Skeleton skeleton, AnimationState state, SkeletonRenderer renderer, float x, float y) {
            this.skeleton = skeleton;
            this.state = state;
            this.renderer = renderer;
            this.x = x;
            this.y = y;
        }
    }

    protected void setSkeletonScale(Skeleton skeleton, float widthPercentage, float heightPercentage) {
        if (skeleton != null) {
            float screenWidth = viewport.getWorldWidth();
            float screenHeight = viewport.getWorldHeight();

            float skeletonWidth = screenWidth * widthPercentage;
            float skeletonHeight = screenHeight * heightPercentage;

            float scaleX = skeletonWidth / skeleton.getData().getWidth();
            float scaleY = skeletonHeight / skeleton.getData().getHeight();

            float scale = Math.min(scaleX, scaleY);

            skeleton.setScale(scale, scale);
            Gdx.app.log("BaseScreen", skeleton.getData().getName() + " skeleton scale set to " + scale);
        }
    }

    protected void setSkeletonPosition(Skeleton skeleton, float x, float y) {
        if (skeleton != null) {
            skeleton.setPosition(x, y);
            Gdx.app.log("BaseScreen", skeleton.getData().getName() + " skeleton position set to x=" + x + " y=" + y);
        }
    }

    public Stage getStage() {
        return stage;
    }
}

