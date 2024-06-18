package com.kandclay;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kandclay.handlers.SpriteSheetAnimationHandler;
import com.kandclay.handlers.SpineAnimationHandler;
import com.kandclay.managers.AudioManager;
import com.kandclay.managers.ConfigurationManager;
import com.kandclay.managers.MyAssetManager;
import com.kandclay.managers.ScreenManager;
import com.kandclay.utils.Constants;
import com.kandclay.utils.Constants.ScreenType;


public class Main extends ApplicationAdapter {
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private MyAssetManager assetManager;
    private AudioManager audioManager;
    private ConfigurationManager configManager;
    private ScreenManager screenManager;
    private SpriteSheetAnimationHandler spriteSheetAnimationHandler;
    private SpineAnimationHandler spineAnimationHandler;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.General.WIDTH, Constants.General.HEIGHT, camera);
        viewport.apply();

        configManager = ConfigurationManager.getInstance();
        assetManager = MyAssetManager.getInstance();
        audioManager = AudioManager.getInstance();
        screenManager = new ScreenManager();
        spriteSheetAnimationHandler = new SpriteSheetAnimationHandler();
        spineAnimationHandler = new SpineAnimationHandler();

        loadInitialAssets();
        screenManager.setScreen(ScreenType.MENU);
    }

    private void loadInitialAssets() {
        assetManager.load(Constants.Skin.JSON, Skin.class);
        assetManager.load(Constants.MainMenu.ATLAS, TextureAtlas.class);
        assetManager.load(Constants.CursorTrail.ATLAS, TextureAtlas.class);
        assetManager.load(Constants.Coin.Yellow.ATLAS, TextureAtlas.class);
        assetManager.load(Constants.Coin.Red.ATLAS, TextureAtlas.class);
        assetManager.load(Constants.Background.PATH, Texture.class);
        assetManager.load(Constants.MainAnimation.ATLAS, TextureAtlas.class);

        // Load bitmap font
        assetManager.load(Constants.Font.FONT_FNT, BitmapFont.class);

        assetManager.finishLoading();
        addFontsToSkin();
    }


    private void addFontsToSkin() {
        Skin skin = assetManager.get(Constants.Skin.JSON, Skin.class);
        BitmapFont customFont = assetManager.get(Constants.Font.FONT_FNT, BitmapFont.class);
        skin.add(Constants.Font.FONT, customFont, BitmapFont.class);

        Label.LabelStyle customLabelStyle = new Label.LabelStyle();
        customLabelStyle.font = customFont;
        skin.add(Constants.Font.LABEL, customLabelStyle);

        // Add ButtonStyle
        TextButton.TextButtonStyle customButtonStyle = new TextButton.TextButtonStyle();
        customButtonStyle.font = customFont;
        customButtonStyle.up = skin.getDrawable("default-rect");
        customButtonStyle.down = skin.getDrawable("default-rect-down");
        customButtonStyle.checked = skin.getDrawable("default-rect");
        skin.add(Constants.Font.BUTTON, customButtonStyle);
    }

    @Override
    public void render() {
        screenManager.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        screenManager.resize(width, height);
    }

    @Override
    public void pause() {
        screenManager.pause();
    }

    @Override
    public void resume() {
        screenManager.resume();
    }

    @Override
    public void dispose() {
        if (screenManager != null) {
            screenManager.dispose();
        }
        if (assetManager != null) {
            assetManager.dispose();
        }
        if (audioManager != null) {
            audioManager.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }
}

