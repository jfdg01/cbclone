package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.managers.ScreenManager;
import com.kandclay.utils.Constants;

public class InnerScreen extends BaseScreen {
    private Texture backgroundTexture;

    private SkeletonRenderer renderer;
    private Skeleton coinSkeleton;
    private AnimationState coinState;

    public InnerScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        super(spineAnimationHandler, screenManager);
        camera = new OrthographicCamera();
        viewport = new FitViewport(200, 200, camera);
    }

    @Override
    public void show() {
        super.show();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Needed otherwise it doesn't render
        backgroundTexture = assetManager.get(Constants.Background.PATH, Texture.class);

        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);

        initializeCoinAnimation();
        setSkeletonScale(coinSkeleton, Constants.MainAnimationScreen.COIN_WIDTH_PERCENTAGE, Constants.MainAnimationScreen.COIN_HEIGHT_PERCENTAGE); // Adjust the percentages as needed
        setSkeletonPosition(coinSkeleton, viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
    }

    private void initializeCoinAnimation() {
        String atlasPath = Constants.MainAnimationScreen.YellowCoin.ATLAS;
        String skeletonPath = Constants.MainAnimationScreen.YellowCoin.JSON;
        coinSkeleton = spineAnimationHandler.createSkeleton(atlasPath, skeletonPath);
        coinState = spineAnimationHandler.createAnimationState(coinSkeleton);

        setSkeletonScale(coinSkeleton, Constants.MainAnimationScreen.COIN_WIDTH_PERCENTAGE, Constants.MainAnimationScreen.COIN_HEIGHT_PERCENTAGE); // Adjust the percentages as needed
        setSkeletonPosition(coinSkeleton, viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);

        coinState.setAnimation(0, "animation", true);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        // Coin animation
        coinState.update(delta);
        coinState.apply(coinSkeleton);
        coinSkeleton.updateWorldTransform();
        renderer.draw(batch, coinSkeleton);
        batch.end();

        // Render inner screen elements (if any)
        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(200, 200, false); // Update size if necessary
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        stage.getViewport().update(width, height, true);
        setSkeletonScale(coinSkeleton, Constants.MainAnimationScreen.COIN_WIDTH_PERCENTAGE, Constants.MainAnimationScreen.COIN_HEIGHT_PERCENTAGE); // Adjust the percentages as needed
        setSkeletonPosition(coinSkeleton, viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}

