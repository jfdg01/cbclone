package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.managers.ScreenManager;
import com.kandclay.utils.Constants;
import com.kandclay.utils.HairColor;
import com.kandclay.utils.ScreenType;

import javax.swing.text.View;

public class ConfigurationScreen extends BaseScreen {
    private Slider volumeSlider;
    private TextButton backButton;
    private TextButton hairColorButton;
    private TextButton coinColorButton;
    private HairColor currentHairColor;
    private boolean isYellowCoin;

    public ConfigurationScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        super(spineAnimationHandler, screenManager);
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        super.show();

        setViewport(new ScreenViewport(getCamera()));

        Skin skin = assetManager.get(Constants.Skin.JSON, Skin.class);
        float savedVolume = configManager.getPreference("volume", Constants.Audio.DEFAULT_VOLUME);
        String savedHairColor = configManager.getPreference("hairColor", HairColor.BLONDE.toString());
        isYellowCoin = configManager.getPreference("coinColor", true);

        currentHairColor = HairColor.valueOf(savedHairColor);

        volumeSlider = new Slider(0, 1, 0.01f, false, skin);
        volumeSlider.setValue(savedVolume);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                audioManager.setVolume(volume);
                configManager.setPreference("volume", volume);
            }
        });

        backButton = new TextButton("Back", skin, Constants.Font.BUTTON);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenManager.setScreen(ScreenType.MENU);
            }
        });

        hairColorButton = new TextButton("Hair Color: " + currentHairColor, skin, Constants.Font.BUTTON);
        hairColorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentHairColor = currentHairColor.next();
                hairColorButton.setText("Hair Color: " + currentHairColor);
                configManager.setPreference("hairColor", currentHairColor.toString());
            }
        });

        coinColorButton = new TextButton("Coin Color: " + (isYellowCoin ? "Yellow" : "Red"), skin, Constants.Font.BUTTON);
        coinColorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isYellowCoin = !isYellowCoin;
                coinColorButton.setText("Coin Color: " + (isYellowCoin ? "Yellow" : "Red"));
                configManager.setPreference("coinColor", isYellowCoin);
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(new Label("Options", skin, Constants.Font.LABEL)).padBottom(Constants.UIButtons.PADDING).row();
        table.add(new Label("Volume", skin, Constants.Font.LABEL)).padBottom(Constants.UIButtons.PADDING).row();
        table.add(volumeSlider).width(Constants.UIButtons.SLIDER_WIDTH).padBottom(Constants.UIButtons.PADDING).row();
        table.add(hairColorButton).width(Constants.UIButtons.CONTROL_BUTTON_WIDTH).height(Constants.UIButtons.CONTROL_BUTTON_HEIGHT).padBottom(Constants.UIButtons.PADDING).row();
        table.add(coinColorButton).width(Constants.UIButtons.CONTROL_BUTTON_WIDTH).height(Constants.UIButtons.CONTROL_BUTTON_HEIGHT).padBottom(Constants.UIButtons.PADDING).row();
        table.add(backButton).width(Constants.UIButtons.BACK_BUTTON_WIDTH).height(Constants.UIButtons.CONTROL_BUTTON_HEIGHT).padTop(Constants.UIButtons.PADDING);

        getStage().addActor(table);

        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        getViewport().apply();
        getBatch().setProjectionMatrix(getViewport().getCamera().combined);

        getStage().act(delta);
        getStage().draw();

        renderTrail(delta, getBatch(), getViewport());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}

