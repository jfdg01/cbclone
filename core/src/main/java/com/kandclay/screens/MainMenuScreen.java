package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.kandclay.utils.ScreenType;

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
    private boolean isInitialAnimationFinished = false;

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
        hoverStates.put(Constants.MainMenuScreen.BUTTON_PLAY_NAME, false);
        hoverStates.put(Constants.MainMenuScreen.BUTTON_QUIT_NAME, false);
        hoverStates.put(Constants.MainMenuScreen.BUTTON_SETTINGS_NAME, false);

        stage.addListener(new InputListener() {
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

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void initializeAnimations() {
        String atlasPath = Constants.MainMenuScreen.ATLAS;
        String skeletonPath = Constants.MainMenuScreen.JSON;

        skeleton = spineAnimationHandler.createSkeleton(atlasPath, skeletonPath);
        state = spineAnimationHandler.createAnimationState(skeleton);

        setSkeletonScale(skeleton, Constants.MainMenuScreen.SKEL_WIDTH_PERCENTAGE, Constants.MainMenuScreen.SKEL_HEIGHT_PERCENTAGE); // Adjust the percentages as needed
        setSkeletonPosition(skeleton, viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
        state.setAnimation(0, "animation", false);

        state.addListener(new AnimationState.AnimationStateListener() {
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
        updateHoverState(x, y, Constants.MainMenuScreen.BUTTON_PLAY_NAME, 1, "Buttons/PlayHoverIn", "Buttons/PlayHoverOut");
        updateHoverState(x, y, Constants.MainMenuScreen.BUTTON_QUIT_NAME, 2, "Buttons/QuitHoverIn", "Buttons/QuitHoverOut");
        updateHoverState(x, y, Constants.MainMenuScreen.BUTTON_SETTINGS_NAME, 3, "Buttons/SettingsHoverIn", "Buttons/SettingsHoverOut");
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
        if (isHoveringButton(x, y, Constants.MainMenuScreen.BUTTON_PLAY_NAME)) {
            playButtonPressAnimation(Constants.MainMenuScreen.BUTTON_PLAY_NAME, "Buttons/PlayPress", ScreenType.GAME);
        } else if (isHoveringButton(x, y, Constants.MainMenuScreen.BUTTON_QUIT_NAME)) {
            playButtonPressAnimation(Constants.MainMenuScreen.BUTTON_QUIT_NAME, "Buttons/QuitPress", null);
        } else if (isHoveringButton(x, y, Constants.MainMenuScreen.BUTTON_SETTINGS_NAME)) {
            playButtonPressAnimation(Constants.MainMenuScreen.BUTTON_SETTINGS_NAME, "Buttons/SettingsPress", ScreenType.OPTIONS);
        }
    }

    private void playButtonPressAnimation(String buttonName, final String animationName, final ScreenType screenType) {
        Gdx.app.log("MainMenuScreen", "Playing button press animation: " + animationName);
        state.setAnimation(4, animationName, false).setListener(new AnimationState.AnimationStateListener() {
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

        state.update(delta * speedMultiplier);
        state.apply(skeleton);
        skeleton.updateWorldTransform();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        renderer.draw(batch, skeleton);
        super.renderTrail(delta);
        batch.end();

//        setSkeletonPosition(skeleton, viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
//        Gdx.app.log("MainMenuScreen", "Render: Skeleton position x=" + skeleton.getX() + " y=" + skeleton.getY());
    }

    private void renderDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        drawDebugBounds(Constants.MainMenuScreen.BUTTON_PLAY_NAME);
        drawDebugBounds(Constants.MainMenuScreen.BUTTON_QUIT_NAME);
        drawDebugBounds(Constants.MainMenuScreen.BUTTON_SETTINGS_NAME);
        shapeRenderer.end();
    }

    private void drawDebugBounds(String buttonName) {
        Rectangle bounds = getButtonBounds(buttonName);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setSkeletonScale(skeleton, Constants.MainMenuScreen.SKEL_WIDTH_PERCENTAGE, Constants.MainMenuScreen.SKEL_HEIGHT_PERCENTAGE);
        setSkeletonPosition(skeleton, viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
