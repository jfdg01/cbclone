package com.kandclay.utils;

public class Constants {

    public static class General {
        public static final int WIDTH = 1600;
        public static final int HEIGHT = 1600;
        public static final String TITLE = "K and Clay";
    }

    public static class Animation {
        public static final int NUM_COLS = 28; // Number of columns in the sprite sheet
        public static final int NUM_ROWS = 28; // Number of rows in the sprite sheet
        public static final float FRAME_DURATION = 5f / 100f; // Duration of each frame
    }

    public static class Game {
        public static final float ROTATION_SPEED = 1f / 20f; // Speed factor for rotation
        public static final float LERP_FACTOR = 0.1f; // Factor for interpolation
    }

    public static class Buttons {
        public static final int BUTTON_WIDTH = 100;
        public static final int BUTTON_HEIGHT = 80;
        public static final int PADDING = 5;
        public static final int SLIDER_WIDTH = 300;
        public static final int CONTROL_BUTTON_HEIGHT = 50;
        public static final int BACK_BUTTON_WIDTH = 150;
        public static final int CONTROL_BUTTON_WIDTH = 300;
    }

    public static class MainAnimation {
        public static final String BUTTON_1X = "1x";
        public static final String BUTTON_2X = "2x";
        public static final String BUTTON_3X = "3x";
        public static final String ATLAS = "spine/speedButtons.atlas";
        public static final String JSON = "spine/speedButtons.json";
    }

    public static class Audio {
        public static final float DEFAULT_VOLUME = 1f;
    }

    public static class Cursor {
        public static final int HOTSPOT_X = 0;
        public static final int HOTSPOT_Y = 0;
        public static final String IMAGE_PATH = "cursor.png";
    }

    public static class TrailDot {
        public static final int NUMBER_OF_COLORS = 360; // Maintain at 360 for full color rotation
        public static final float SATURATION = 1.0f;
        public static final float VALUE = 1.0f;
        public static final float ALPHA = 0.5f;
        public static final float MIN_SCALE = 0.2f;
        public static final float MAX_SCALE = 0.5f;
        public static final int MIN_ROTATION = 0;
        public static final int MAX_ROTATION = 359;
    }

    public static class MainMenuScreen {
        public static final String ATLAS = "spine/menu.atlas";
        public static final String JSON = "spine/menu.json";
        public static final String BUTTON_PLAY = "play";
        public static final String BUTTON_QUIT = "quit";
        public static final String BUTTON_SETTINGS = "settings";
        public static final float LOGO_WIDTH_PERCENTAGE = 1.3f;
        public static final float LOGO_HEIGHT_PERCENTAGE = 1.3f;
    }

    public static class MainAnimationScreen {
        public static final float COIN_WIDTH_PERCENTAGE = 0.4f;
        public static final float COIN_HEIGHT_PERCENTAGE = 0.4f;
        public static final float BUTTONS_WIDTH_PERCENTAGE = 0.3f;
        public static final float BUTTONS_HEIGHT_PERCENTAGE = 0.3f;
    }

    public static class CursorTrail {
        public static final String ATLAS = "spine/trailDot.atlas";
        public static final String JSON = "spine/trailDot.json";
    }

    public static class Skin {
        public static final String JSON = "skin/default/skin/uiskin.json";
    }

    public static class Coin {
        public static class Yellow {
            public static final String ATLAS = "spine/coin-yellow.atlas";
            public static final String JSON = "spine/coin-yellow.json";
        }

        public static class Red {
            public static final String ATLAS = "spine/coin-red.atlas";
            public static final String JSON = "spine/coin-red.json";
        }
    }

    public static class Sounds {
        public static final String OOF = "sounds/sound.ogg";
    }

    public static class Font {
        public static final String PATH = "fonts/Playground.ttf";
        public static final String BUTTON = "custom-button";
        public static final String LABEL = "custom-label";
        public static final String FONT = "custom-font";
        public static final String FONT_FNT = "com/badlogic/gdx/utils/lsans-15.fnt";
    }

    public static class Background {
        public static final String PATH = "background.png";
    }
}
