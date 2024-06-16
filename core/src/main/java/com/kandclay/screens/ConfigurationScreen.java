package com.kandclay.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.managers.ScreenManager;
import com.kandclay.utils.Constants;

public class ConfigurationScreen extends BaseScreen {
    private Slider volumeSlider;
    private TextButton backButton;
    private TextButton hairColorButton;
    private TextButton heightButton;
    private TextButton coinColorButton;

    private Constants.HairColor currentHairColor;
    private Constants.Height currentHeight;
    private boolean isYellowCoin;

    public ConfigurationScreen(SpineAnimationHandler spineAnimationHandler, ScreenManager screenManager) {
        super(spineAnimationHandler, screenManager);
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        super.show();

        Skin skin = assetManager.get(Constants.Skin.JSON, Skin.class);

        // Load preferences
        float savedVolume = configManager.getPreference("volume", Constants.Audio.DEFAULT_VOLUME);
        String savedHairColor = configManager.getPreference("hairColor", Constants.HairColor.BLONDE.toString());
        String savedHeight = configManager.getPreference("height", Constants.Height.AVERAGE.toString());
        isYellowCoin = configManager.getPreference("coinColor", true); // Default to yellow coin

        currentHairColor = Constants.HairColor.valueOf(savedHairColor);
        currentHeight = Constants.Height.valueOf(savedHeight);

        // Create a slider for volume control
        volumeSlider = new Slider(0, 1, 0.01f, false, skin);
        volumeSlider.setValue(savedVolume);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                audioManager.setVolume(volume);
                configManager.setPreference("volume", volume); // Save volume preference
            }
        });

        // Create a back button with the new font style
        backButton = new TextButton("Back", skin, Constants.Font.BUTTON);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenManager.setScreen(Constants.ScreenType.MENU);
            }
        });

        // Create other buttons with the new font style
        hairColorButton = new TextButton("Hair Color: " + currentHairColor, skin,  Constants.Font.BUTTON);
        hairColorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentHairColor = currentHairColor.next();
                hairColorButton.setText("Hair Color: " + currentHairColor);
                configManager.setPreference("hairColor", currentHairColor.toString()); // Save hair color preference
            }
        });

        heightButton = new TextButton("Height: " + currentHeight, skin,  Constants.Font.BUTTON);
        heightButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentHeight = currentHeight.next();
                heightButton.setText("Height: " + currentHeight);
                configManager.setPreference("height", currentHeight.toString()); // Save height preference
            }
        });

        coinColorButton = new TextButton("Coin Color: " + (isYellowCoin ? "Yellow" : "Red"), skin,  Constants.Font.BUTTON);
        coinColorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isYellowCoin = !isYellowCoin;
                coinColorButton.setText("Coin Color: " + (isYellowCoin ? "Yellow" : "Red"));
                configManager.setPreference("coinColor", isYellowCoin); // Save coin color preference
            }
        });

        // Arrange the UI elements in a table
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(new Label("Options", skin, Constants.Font.LABEL)).padBottom(Constants.Buttons.PADDING).row();
        table.add(new Label("Volume", skin, Constants.Font.LABEL)).padBottom(Constants.Buttons.PADDING).row();
        table.add(volumeSlider).width(Constants.Buttons.SLIDER_WIDTH).padBottom(Constants.Buttons.PADDING).row();
        table.add(hairColorButton).width(Constants.Buttons.CONTROL_BUTTON_WIDTH).height(Constants.Buttons.CONTROL_BUTTON_HEIGHT).padBottom(Constants.Buttons.PADDING).row();
        table.add(heightButton).width(Constants.Buttons.CONTROL_BUTTON_WIDTH).height(Constants.Buttons.CONTROL_BUTTON_HEIGHT).padBottom(Constants.Buttons.PADDING).row();
        table.add(coinColorButton).width(Constants.Buttons.CONTROL_BUTTON_WIDTH).height(Constants.Buttons.CONTROL_BUTTON_HEIGHT).padBottom(Constants.Buttons.PADDING).row();
        table.add(backButton).width(Constants.Buttons.BACK_BUTTON_WIDTH).height(Constants.Buttons.CONTROL_BUTTON_HEIGHT).padTop(Constants.Buttons.PADDING);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.begin();
        // Draw the stage which includes UI elements
        stage.act(delta);
        stage.draw();
        // Draw the trail dots
        renderTrail(delta);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}

