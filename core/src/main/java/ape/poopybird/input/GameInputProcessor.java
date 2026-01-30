package ape.poopybird.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ape.poopybird.util.Constants;

public class GameInputProcessor extends InputAdapter {
    private final Vector3 inputDirection;
    private boolean poopRequested;
    private boolean useMouseInput;

    // Mouse input
    private final Vector2 mousePosition;
    private final Vector2 screenCenter;

    public GameInputProcessor() {
        this.inputDirection = new Vector3();
        this.mousePosition = new Vector2();
        this.screenCenter = new Vector2();
        this.poopRequested = false;
        this.useMouseInput = true;

        updateScreenCenter();
    }

    public void updateScreenCenter() {
        screenCenter.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
    }

    public void update() {
        inputDirection.setZero();

        // Keyboard input (WASD / Arrows)
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            inputDirection.z -= 1;
            useMouseInput = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            inputDirection.z += 1;
            useMouseInput = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            inputDirection.x -= 1;
            useMouseInput = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            inputDirection.x += 1;
            useMouseInput = false;
        }

        // Mouse input - direction based on mouse position relative to center
        if (inputDirection.isZero() && useMouseInput) {
            mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
            float dx = mousePosition.x - screenCenter.x;
            float dy = mousePosition.y - screenCenter.y;

            // Normalize and apply deadzone
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            float maxDistance = Math.min(screenCenter.x, screenCenter.y) * 0.8f;

            if (distance > 20) { // Deadzone
                float normalizedDistance = Math.min(distance / maxDistance, 1f);
                inputDirection.x = (dx / distance) * normalizedDistance;
                inputDirection.z = (dy / distance) * normalizedDistance; // Y screen -> Z world
            }
        }

        // Normalize if using keyboard
        if (inputDirection.len2() > 1) {
            inputDirection.nor();
        }

        // Check for poop input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            poopRequested = true;
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            poopRequested = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        useMouseInput = true;
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.W || keycode == Input.Keys.A ||
            keycode == Input.Keys.S || keycode == Input.Keys.D ||
            keycode == Input.Keys.UP || keycode == Input.Keys.DOWN ||
            keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT) {
            useMouseInput = false;
        }
        return false;
    }

    public Vector3 getInputDirection() {
        return inputDirection;
    }

    public boolean isPoopRequested() {
        return poopRequested;
    }

    public void clearPoopRequest() {
        poopRequested = false;
    }

    public void reset() {
        inputDirection.setZero();
        poopRequested = false;
        useMouseInput = true;
    }
}
