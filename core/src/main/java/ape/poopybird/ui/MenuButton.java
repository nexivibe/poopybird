package ape.poopybird.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MenuButton {
    private final Rectangle bounds;
    private final String text;
    private final BitmapFont font;
    private final GlyphLayout layout;
    private boolean hovered;
    private boolean pressed;

    private static final Color NORMAL_COLOR = new Color(0.3f, 0.5f, 0.3f, 1f);
    private static final Color HOVER_COLOR = new Color(0.4f, 0.7f, 0.4f, 1f);
    private static final Color PRESSED_COLOR = new Color(0.2f, 0.4f, 0.2f, 1f);
    private static final Color BORDER_COLOR = new Color(0.5f, 0.8f, 0.5f, 1f);
    private static final Color TEXT_COLOR = Color.WHITE;

    public MenuButton(float x, float y, float width, float height, String text, BitmapFont font) {
        this.bounds = new Rectangle(x, y, width, height);
        this.text = text;
        this.font = font;
        this.layout = new GlyphLayout(font, text);
        this.hovered = false;
        this.pressed = false;
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // Draw button background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (pressed) {
            shapeRenderer.setColor(PRESSED_COLOR);
        } else if (hovered) {
            shapeRenderer.setColor(HOVER_COLOR);
        } else {
            shapeRenderer.setColor(NORMAL_COLOR);
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(BORDER_COLOR);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        // Draw text
        batch.begin();
        font.setColor(TEXT_COLOR);
        float textX = bounds.x + (bounds.width - layout.width) / 2;
        float textY = bounds.y + (bounds.height + layout.height) / 2;
        font.draw(batch, text, textX, textY);
        batch.end();
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean isHovered() {
        return hovered;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }

    public void setSize(float width, float height) {
        bounds.setSize(width, height);
    }
}
