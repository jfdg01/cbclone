package com.kandclay.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import com.kandclay.managers.MyAssetManager;

public class SpineAnimationHandler {
    private MyAssetManager assetManager;

    public SpineAnimationHandler() {
        this.assetManager = MyAssetManager.getInstance();
    }

    public Skeleton createSkeleton(String atlasPath, String skeletonPath) {
        TextureAtlas atlas = assetManager.get(atlasPath, TextureAtlas.class);
        SkeletonJson json = new SkeletonJson(atlas);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(skeletonPath));
        return new Skeleton(skeletonData);
    }

    public AnimationState createAnimationState(Skeleton skeleton) {
        AnimationStateData stateData = new AnimationStateData(skeleton.getData());
        return new AnimationState(stateData);
    }
}

