package com.kandclay.handlers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.graphics.Texture;
import com.kandclay.utils.Constants;
import com.kandclay.managers.MyAssetManager;

public class SpriteSheetAnimationHandler {
    private MyAssetManager assetManager;

    public SpriteSheetAnimationHandler() {
        this.assetManager = MyAssetManager.getInstance();
    }

    public Animation<TextureRegion> createAnimationFromAssetManager(String regionName) {
        String name = "sprites/anim/" + regionName + ".png";
        Texture texture = assetManager.get(name, Texture.class);

        int frameWidth = texture.getWidth() / Constants.SpriteAnimationSettings.NUM_COLS;
        int frameHeight = texture.getHeight() / Constants.SpriteAnimationSettings.NUM_ROWS;

        TextureRegion[][] tmpFrames = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] animationFrames = new TextureRegion[Constants.SpriteAnimationSettings.NUM_ROWS * Constants.SpriteAnimationSettings.NUM_COLS];

        int index = 0;
        for (int i = 0; i < Constants.SpriteAnimationSettings.NUM_ROWS; i++) {
            for (int j = 0; j < Constants.SpriteAnimationSettings.NUM_COLS; j++) {
                animationFrames[index++] = tmpFrames[i][j];
            }
        }

        return new Animation<TextureRegion>(Constants.SpriteAnimationSettings.FRAME_DURATION, animationFrames);
    }

    // Additional helper method to handle animations created by hand
    public Animation<TextureRegion> createAnimationFromCustomFrames(TextureRegion[] frames, float frameDuration) {
        return new Animation<TextureRegion>(frameDuration, frames);
    }
}

