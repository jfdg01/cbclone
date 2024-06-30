package com.kandclay.vp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ViewportTest extends ApplicationAdapter {
    private TextureRegion textureRegion;
    private TextureRegion soldierRegion;
    private TextureRegion healthRegion;
    private TextureRegion ammoRegion;
    private SpriteBatch spriteBatch;
    private ScreenViewport leftViewport;
    private ScreenViewport rightViewport;
    private FitViewport fitViewport;
    private Skin skin;
    private Stage leftStage;
    private Stage rightStage;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        Texture texture = new Texture(Gdx.files.internal("vp/texture.png"));
        textureRegion = new TextureRegion(texture);

        texture = new Texture(Gdx.files.internal("vp/infantry.png"));
        soldierRegion = new TextureRegion(texture);

        texture = new Texture(Gdx.files.internal("vp/health.png"));
        healthRegion = new TextureRegion(texture);

        texture = new Texture(Gdx.files.internal("vp/ammo.png"));
        ammoRegion = new TextureRegion(texture);

        skin = new Skin(Gdx.files.internal("skin/default/skin/uiskin.json"));

        leftViewport = new ScreenViewport();
        leftStage = new Stage(leftViewport);

        rightViewport = new ScreenViewport();
        rightStage = new Stage(rightViewport);

        fitViewport = new FitViewport(800, 800);

        Table root = new Table();
        root.setFillParent(true);
        root.pad(10);
        leftStage.addActor(root);

        root.defaults().space(10);
        TextureRegionDrawable drawable = new TextureRegionDrawable(soldierRegion);
        Image image = new Image(drawable);
        image.setScaling(Scaling.fit);
        root.add(image);

        root.row();
        Label label = new Label("INFANTRY", skin);
        root.add(label);

        root.row();
        drawable = new TextureRegionDrawable(healthRegion);
        image = new Image(drawable);
        image.setScaling(Scaling.fit);
        root.add(image);

        root = new Table();
        root.setFillParent(true);
        root.pad(10);
        rightStage.addActor(root);

        root.defaults().space(10);
        drawable = new TextureRegionDrawable(ammoRegion);
        image = new Image(drawable);
        image.setScaling(Scaling.fit);
        root.add(image);

        root.row();
        label = new Label("x23", skin);
        root.add(label);

        root.row();
        label = new Label("Primary Objective: Proceed to main living space and erradicate all rodents", skin);
        label.setWrap(true);
        root.add(label).growX();
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.DARK_GRAY);

        leftStage.act();
        rightStage.act();

        leftViewport.apply();
        leftStage.draw();

        rightViewport.apply();
        rightStage.draw();

        fitViewport.apply();
        spriteBatch.setProjectionMatrix(fitViewport.getCamera().combined);
        spriteBatch.begin();
        spriteBatch.draw(textureRegion, 0, 0);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height, true);
        leftViewport.update(fitViewport.getLeftGutterWidth(), height, true);
        leftViewport.setScreenPosition(0, 0);
        rightViewport.update(fitViewport.getRightGutterWidth(), height, true);
        rightViewport.setScreenPosition(fitViewport.getRightGutterX(), 0);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        textureRegion.getTexture().dispose();
        ammoRegion.getTexture().dispose();
        soldierRegion.getTexture().dispose();
        healthRegion.getTexture().dispose();
        skin.dispose();
    }
}
