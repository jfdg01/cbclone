package com.kandclay.handlers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {

    @Override
    public boolean keyDown(int keycode) {
        // Handle key press event
        switch (keycode) {
            case Input.Keys.UP:
                // Handle up key
                break;
            case Input.Keys.DOWN:
                // Handle down key
                break;
            case Input.Keys.LEFT:
                // Handle left key
                break;
            case Input.Keys.RIGHT:
                // Handle right key
                break;
            case Input.Keys.ESCAPE:
                // Handle escape key
                break;
            default:
                break;
        }
        return true; // Return true to indicate the event was handled
    }

    @Override
    public boolean keyUp(int keycode) {
        // Handle key release event
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        // Handle character typed event
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Handle touch down event
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Handle touch up event
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Handle touch dragged event
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Handle mouse moved event
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Handle scrolling event
        return true;
    }
}

