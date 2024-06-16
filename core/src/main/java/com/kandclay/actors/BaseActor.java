package com.kandclay.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BaseActor extends Actor {
    private TextureRegion textureRegion;

    public BaseActor(Texture texture) {
        this.textureRegion = new TextureRegion(texture);
        setBounds(getX(), getY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
        if (isVisible()) {
            batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(),
                getScaleX(), getScaleY(), getRotation());
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Any additional behavior for BaseActor can be added here
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        // Update bounds or any other properties dependent on position
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        // Update bounds or any other properties dependent on size
    }

    public void dispose() {
        if (textureRegion.getTexture() != null) {
            textureRegion.getTexture().dispose();
        }
    }
}
