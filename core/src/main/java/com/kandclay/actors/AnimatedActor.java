package com.kandclay.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kandclay.utils.Constants;

public class AnimatedActor extends Actor {
    private Animation<TextureRegion> animation;
    private float stateTime;
    private float targetX, targetY;

    public AnimatedActor(Animation<TextureRegion> animation) {
        this.animation = animation;
        this.stateTime = 0f;
        TextureRegion initialFrame = animation.getKeyFrame(0);
        setBounds(getX(), getY(), initialFrame.getRegionWidth(), initialFrame.getRegionHeight());
        setVisible(false); // Initially set to not visible
    }

    public void setAnimation(Animation<TextureRegion> newAnimation) {
        this.animation = newAnimation;
        this.stateTime = 0f;
        TextureRegion initialFrame = newAnimation.getKeyFrame(0);
        setBounds(getX(), getY(), initialFrame.getRegionWidth(), initialFrame.getRegionHeight());
    }

    public void setTargetPosition(float x, float y) {
        this.targetX = x;
        this.targetY = y;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        // Interpolate to target position
        float newX = getX() + (targetX - getX()) * Constants.Game.LERP_FACTOR;
        float newY = getY() + (targetY - getY()) * Constants.Game.LERP_FACTOR;
        setPosition(newX, newY);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isVisible()) return; // Only draw if visible
        super.draw(batch, parentAlpha);
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }
}

