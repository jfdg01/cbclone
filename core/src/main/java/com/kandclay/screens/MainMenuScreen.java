package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.managers.ScreenManager;
import com.kandclay.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class MainMenuScreen extends BaseScreen {

    private SkeletonRenderer renderer;
    private Skeleton skeleton;
    private AnimationState state;
    private BitmapFont font;
    private float speedMultiplier = 1f;
    private Map<String, Boolean> hoverStates;
    private Texture backgroundTexture;
    private ShapeRenderer shapeRenderer;

    public MainMenuScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        super(spineAnimationHandler, screenManager);
    }

    @Override
    public void show() {
        super.show();

        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);

        initializeAnimations();

        font = new BitmapFont();
        backgroundTexture = assetManager.get(Constants.Background.PATH, Texture.class);
        shapeRenderer = new ShapeRenderer();

        hoverStates = new HashMap<String, Boolean>();
        hoverStates.put(Constants.MainMenu.BUTTON_PLAY, false);
        hoverStates.put(Constants.MainMenu.BUTTON_QUIT, false);
        hoverStates.put(Constants.MainMenu.BUTTON_SETTINGS, false);

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

    // Initialize animations and other setup
    private void initializeAnimations() {
        String atlasPath = Constants.MainMenu.ATLAS;
        String skeletonPath = Constants.MainMenu.JSON;

        skeleton = spineAnimationHandler.createSkeleton(atlasPath, skeletonPath);
        state = spineAnimationHandler.createAnimationState(skeleton);

        setSkeletonScale();
        setSkeletonPosition();
        state.setAnimation(0, "animation", false);
    }

    // Handle hover states for buttons
    private void handleHover(float x, float y) {
        updateHoverState(x, y, Constants.MainMenu.BUTTON_PLAY, 1, "Buttons/PlayHoverIn", "Buttons/PlayHoverOut");
        updateHoverState(x, y, Constants.MainMenu.BUTTON_QUIT, 2, "Buttons/QuitHoverIn", "Buttons/QuitHoverOut");
        updateHoverState(x, y, Constants.MainMenu.BUTTON_SETTINGS, 3, "Buttons/SettingsHoverIn", "Buttons/SettingsHoverOut");
    }

    private void updateHoverState(float x, float y, String buttonName, int trackIndex, String hoverInAnim, String hoverOutAnim) {
        boolean isHovered = isHoveringButton(x, y, buttonName);
        boolean wasHovered = hoverStates.get(buttonName);

        if (isHovered && !wasHovered) {
            state.setAnimation(trackIndex, hoverInAnim, false);
        } else if (!isHovered && wasHovered) {
            state.setAnimation(trackIndex, hoverOutAnim, false);
        }

        hoverStates.put(buttonName, isHovered);
    }

    private void handleClick(float x, float y) {
        if (isHoveringButton(x, y, Constants.MainMenu.BUTTON_PLAY)) {
            screenManager.setScreen(Constants.ScreenType.GAME);
        } else if (isHoveringButton(x, y, Constants.MainMenu.BUTTON_QUIT)) {
            Gdx.app.exit();
        } else if (isHoveringButton(x, y, Constants.MainMenu.BUTTON_SETTINGS)) {
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

        Slot slot = skeleton.findSlot(buttonName);
        if (slot == null || !(slot.getAttachment() instanceof RegionAttachment)) return new Rectangle();

        RegionAttachment attachment = (RegionAttachment) slot.getAttachment();
        if (attachment == null) return new Rectangle();

        // Calculate the world position and dimensions of the button
        float[] vertices = new float[8];
        attachment.computeWorldVertices(slot.getBone(), vertices, 0, 2);

        // Calculate bounds based on the vertices
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

        state.update(delta * speedMultiplier);
        state.apply(skeleton);
        skeleton.updateWorldTransform();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Draw the background
        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        renderer.draw(batch, skeleton);
        super.renderTrail(delta);
        batch.end();

        setSkeletonPosition();

        //renderDebug();
    }

    private void renderDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        drawDebugBounds(Constants.MainMenu.BUTTON_PLAY);
        drawDebugBounds(Constants.MainMenu.BUTTON_QUIT);
        drawDebugBounds(Constants.MainMenu.BUTTON_SETTINGS);
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
        setSkeletonScale();
        setSkeletonPosition();
    }

    private void setSkeletonPosition() {
        if (skeleton != null) {
            float centerX = viewport.getWorldWidth() / 2;
            float centerY = viewport.getWorldHeight() / 2;
            skeleton.setPosition(centerX, centerY);
        }
    }

    private void setSkeletonScale() {
        if (skeleton != null) {
            float scale = viewport.getWorldWidth() / skeleton.getData().getWidth();
            scale *= 1.2f;
            skeleton.setScale(scale, scale);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        // Do not dispose of backgroundTexture here if it's shared
        shapeRenderer.dispose();
    }
}
