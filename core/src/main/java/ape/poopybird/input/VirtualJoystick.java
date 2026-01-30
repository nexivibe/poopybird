package ape.poopybird.input;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class VirtualJoystick {
    private final Vector2 center;
    private final Vector2 knobPosition;
    private final float outerRadius;
    private final float knobRadius;
    private final Vector3 outputDirection;
    private boolean active;
    private int touchPointer;

    public VirtualJoystick(float centerX, float centerY, float outerRadius) {
        this.center = new Vector2(centerX, centerY);
        this.knobPosition = new Vector2(centerX, centerY);
        this.outerRadius = outerRadius;
        this.knobRadius = outerRadius * 0.4f;
        this.outputDirection = new Vector3();
        this.active = false;
        this.touchPointer = -1;
    }

    public boolean touchDown(int screenX, int screenY, int pointer) {
        float invertedY = screenY; // Will be converted in render

        float dx = screenX - center.x;
        float dy = invertedY - center.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= outerRadius) {
            active = true;
            touchPointer = pointer;
            updateKnob(screenX, invertedY);
            return true;
        }
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (active && pointer == touchPointer) {
            updateKnob(screenX, screenY);
            return true;
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer) {
        if (active && pointer == touchPointer) {
            active = false;
            touchPointer = -1;
            knobPosition.set(center);
            outputDirection.setZero();
            return true;
        }
        return false;
    }

    private void updateKnob(float screenX, float screenY) {
        float dx = screenX - center.x;
        float dy = screenY - center.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > outerRadius - knobRadius) {
            // Clamp to edge
            float scale = (outerRadius - knobRadius) / distance;
            dx *= scale;
            dy *= scale;
        }

        knobPosition.set(center.x + dx, center.y + dy);

        // Normalize to -1 to 1 range
        float maxDistance = outerRadius - knobRadius;
        outputDirection.x = dx / maxDistance;
        outputDirection.z = dy / maxDistance; // Y screen -> Z world
        outputDirection.y = 0;
    }

    public void render(ShapeRenderer shapeRenderer) {
        // Outer circle
        shapeRenderer.setColor(new Color(1, 1, 1, 0.3f));
        shapeRenderer.circle(center.x, center.y, outerRadius);

        // Inner knob
        shapeRenderer.setColor(new Color(1, 1, 1, 0.6f));
        shapeRenderer.circle(knobPosition.x, knobPosition.y, knobRadius);
    }

    public Vector3 getDirection() {
        return outputDirection;
    }

    public boolean isActive() {
        return active;
    }

    public void setPosition(float x, float y) {
        center.set(x, y);
        if (!active) {
            knobPosition.set(center);
        }
    }
}
