package com.kandclay.managers;

import com.badlogic.gdx.Gdx;
import com.kandclay.handlers.SpriteSheetAnimationHandler;
import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.screens.*;
import com.kandclay.utils.ScreenType;

public class ScreenManager {

    private BaseScreen currentScreen;
    private final SpriteSheetAnimationHandler spriteSheetAnimationHandler;
    private final SpineAnimationHandler spineAnimationHandler;

    public ScreenManager() {
        MyAssetManager.getInstance();
        AudioManager.getInstance();
        ConfigurationManager.getInstance();
        this.spriteSheetAnimationHandler = new SpriteSheetAnimationHandler();
        this.spineAnimationHandler = new SpineAnimationHandler();
    }

    public void setScreen(ScreenType screenType) {
        if (currentScreen != null) {
            currentScreen.hide();
            currentScreen.dispose();
        }
        switch (screenType) {
            case MENU:
                currentScreen = new MainMenuScreen(spineAnimationHandler, this);
                break;
            case MAIN:
                currentScreen = new MainAnimationScreen(spineAnimationHandler, this);
                break;
            case OPTIONS:
                currentScreen = new ConfigurationScreen(spineAnimationHandler, this);
                break;
        }
        currentScreen.show();
        currentScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render(float delta) {
        if (currentScreen != null) {
            currentScreen.render(delta);
        }
    }

    public void resize(int width, int height) {
        if (currentScreen != null) {
            currentScreen.resize(width, height);
        }
    }

    public void pause() {
        if (currentScreen != null) {
            currentScreen.pause();
        }
    }

    public void resume() {
        if (currentScreen != null) {
            currentScreen.resume();
        }
    }

    public void dispose() {
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    }
}

