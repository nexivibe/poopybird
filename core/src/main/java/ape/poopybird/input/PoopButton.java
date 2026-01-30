package ape.poopybird.input;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class PoopButton {
    private final Vector2 center;
    private final float radius;
    private boolean pressed;
    private boolean justPressed;
    private float cooldownProgress;

    private static final Color READY_COLOR = new Color(0.6f, 0.4f, 0.2f, 0.8f);
    private static final Color COOLDOWN_COLOR = new Color(0.4f, 0.4f, 0.4f, 0.6f);
    private static final Color PRESSED_COLOR = new Color(0.5f, 0.3f, 0.15f, 0.9f);

    public PoopButton(float centerX, float centerY, float radius) {
        this.center = new Vector2(centerX, centerY);
        this.radius = radius;
        this.pressed = false;
        this.justPressed = false;
        this.cooldownProgress = 1f;
    }

    public boolean touchDown(int screenX, int screenY, int pointer) {
        float dx = screenX - center.x;
        float dy = screenY - center.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= radius && cooldownProgress >= 1f) {
            pressed = true;
            justPressed = true;
            return true;
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer) {
        if (pressed) {
            pressed = false;
            return true;
        }
        return false;
    }

    public void render(ShapeRenderer shapeRenderer) {
        // Background circle
        if (pressed) {
            shapeRenderer.setColor(PRESSED_COLOR);
        } else if (cooldownProgress >= 1f) {
            shapeRenderer.setColor(READY_COLOR);
        } else {
            shapeRenderer.setColor(COOLDOWN_COLOR);
        }
        shapeRenderer.circle(center.x, center.y, radius);

        // Cooldown indicator (arc)
        if (cooldownProgress < 1f) {
            shapeRenderer.setColor(READY_COLOR);
            shapeRenderer.arc(center.x, center.y, radius * 0.9f, 90, cooldownProgress * 360);
        }

        // Poop icon (simplified)
        shapeRenderer.setColor(new Color(0.45f, 0.3f, 0.15f, 1f));
        float iconSize = radius * 0.4f;
        // Draw a simple poop shape
        shapeRenderer.circle(center.x, center.y - iconSize * 0.2f, iconSize * 0.8f);
        shapeRenderer.circle(center.x, center.y + iconSize * 0.3f, iconSize * 0.6f);
        shapeRenderer.circle(center.x, center.y + iconSize * 0.7f, iconSize * 0.4f);
    }

    public void setCooldownProgress(float progress) {
        this.cooldownProgress = Math.min(1f, Math.max(0f, progress));
    }

    public boolean isJustPressed() {
        boolean result = justPressed;
        justPressed = false;
        return result;
    }

    public void setPosition(float x, float y) {
        center.set(x, y);
    }
}
