package com.kandclay.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;

public class SpineAnimatedActor extends Actor {
    private Skeleton skeleton;
    private AnimationState state;
    private SkeletonRenderer renderer;

    public SpineAnimatedActor(Skeleton skeleton, AnimationState state) {
        this.skeleton = skeleton;
        this.state = state;
        this.renderer = new SkeletonRenderer();
        this.renderer.setPremultipliedAlpha(true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        state.update(delta);
        state.apply(skeleton);
        skeleton.updateWorldTransform();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
        renderer.draw(batch, skeleton);
    }

    public void setSkeletonPosition(float x, float y) {
        skeleton.setPosition(x, y);
    }

    public void setAnimation(String animationName, boolean loop) {
        state.setAnimation(0, animationName, loop);
    }

    public Skeleton getSkeleton() {
        return skeleton;
    }

    public void setSkeleton(Skeleton skeleton) {
        this.skeleton = skeleton;
    }

    public AnimationState getAnimationState() {
        return state;
    }

    public void setAnimationState(AnimationState state) {
        this.state = state;
    }

    public SkeletonRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(SkeletonRenderer renderer) {
        this.renderer = renderer;
    }
}

