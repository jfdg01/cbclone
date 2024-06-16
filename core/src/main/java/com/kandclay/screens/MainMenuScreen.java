package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.managers.ScreenManager;
import com.kandclay.utils.Constants;

public class MainMenuScreen extends BaseScreen {

    private OrthographicCamera camera;
    private SkeletonRenderer renderer;
    private Skeleton skeleton;
    private AnimationState state;
    private BitmapFont font;
    private float speedMultiplier = 1f;
    private boolean isPlayHovered = false;
    private boolean isQuitHovered = false;
    private boolean isSettingsHovered = false;
    private Texture backgroundTexture;

    public MainMenuScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        super(spineAnimationHandler, screenManager);
    }

    @Override
    public void show() {
        super.show();

        camera = new OrthographicCamera();
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);

        initializeAnimations();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font = new BitmapFont();

        // Load the background texture
        backgroundTexture = assetManager.get("background.jpg", Texture.class);

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
    }

    private void initializeAnimations() {
        String atlasPath = Constants.MainMenu.ATLAS;
        String skeletonPath = Constants.MainMenu.JSON;

        skeleton = spineAnimationHandler.createSkeleton(atlasPath, skeletonPath);
        state = spineAnimationHandler.createAnimationState(skeleton);
        skeleton.setScale(1f, 1f);

        setSkeletonPosition();
        state.setAnimation(0, "animation", false);
    }

    private void handleHover(float x, float y) {
        Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(x, y));

        if (isHoveringButton(stageCoords.x, stageCoords.y, "play")) {
            if (!isPlayHovered) {
                state.setAnimation(1, "Buttons/PlayHoverIn", false);
                isPlayHovered = true;
            }
        } else {
            if (isPlayHovered) {
                state.setAnimation(1, "Buttons/PlayHoverOut", false);
                isPlayHovered = false;
            }
        }

        if (isHoveringButton(stageCoords.x, stageCoords.y, "quit")) {
            if (!isQuitHovered) {
                state.setAnimation(2, "Buttons/QuitHoverIn", false);
                isQuitHovered = true;
            }
        } else {
            if (isQuitHovered) {
                state.setAnimation(2, "Buttons/QuitHoverOut", false);
                isQuitHovered = false;
            }
        }

        if (isHoveringButton(stageCoords.x, stageCoords.y, "settings")) {
            if (!isSettingsHovered) {
                state.setAnimation(3, "Buttons/SettingsHoverIn", false);
                isSettingsHovered = true;
            }
        } else {
            if (isSettingsHovered) {
                state.setAnimation(3, "Buttons/SettingsHoverOut", false);
                isSettingsHovered = false;
            }
        }
    }

    private void handleClick(float x, float y) {
        Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(x, y));

        if (isHoveringButton(stageCoords.x, stageCoords.y, "play")) {
            screenManager.setScreen(Constants.ScreenType.GAME);
        } else if (isHoveringButton(stageCoords.x, stageCoords.y, "quit")) {
            Gdx.app.exit();
        } else if (isHoveringButton(stageCoords.x, stageCoords.y, "settings")) {
            screenManager.setScreen(Constants.ScreenType.OPTIONS);
        }
    }

    private boolean isHoveringButton(float x, float y, String buttonName) {
        Rectangle buttonBounds = getButtonBounds(buttonName);
        return buttonBounds.contains(x, y);
    }

    private Rectangle getButtonBounds(String buttonName) {
        Bone bone = skeleton.findBone(buttonName);
        if (bone == null) return new Rectangle();

        RegionAttachment attachment = (RegionAttachment) skeleton.findSlot(buttonName).getAttachment();
        if (attachment == null) return new Rectangle();

        float buttonX = bone.getWorldX() - (attachment.getWidth() * bone.getScaleX() / 2);
        float buttonY = bone.getWorldY() - (attachment.getHeight() * bone.getScaleY() / 2);
        float buttonWidth = attachment.getWidth() * bone.getScaleX();
        float buttonHeight = attachment.getHeight() * bone.getScaleY();

        buttonY = camera.viewportHeight - buttonY - buttonHeight;

        return new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        state.update(delta * speedMultiplier);
        state.apply(skeleton);
        skeleton.updateWorldTransform();

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Draw the background
        batch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
        renderer.draw(batch, skeleton);
        super.renderTrail(delta);
        batch.end();

        setSkeletonPosition();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.setToOrtho(false, width, height);
        camera.update();
        setSkeletonPosition();
    }

    private void setSkeletonPosition() {
        float centerX = camera.viewportWidth / 2;
        float centerY = camera.viewportHeight / 2;
        skeleton.setPosition(centerX, centerY);
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        backgroundTexture.dispose(); // Dispose the background texture
    }
}
