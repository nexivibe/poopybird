package ape.poopybird.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import ape.poopybird.entities.BirdType;

public class BirdCard {
    private final BirdType birdType;
    private final Rectangle bounds;
    private boolean selected;
    private boolean hovered;

    private static final Color CARD_COLOR = new Color(0.25f, 0.25f, 0.3f, 1f);
    private static final Color SELECTED_COLOR = new Color(0.3f, 0.5f, 0.3f, 1f);
    private static final Color HOVER_COLOR = new Color(0.35f, 0.35f, 0.4f, 1f);
    private static final Color BORDER_COLOR = new Color(0.5f, 0.5f, 0.6f, 1f);
    private static final Color SELECTED_BORDER = new Color(0.5f, 0.9f, 0.5f, 1f);

    public BirdCard(BirdType birdType, float x, float y, float width, float height) {
        this.birdType = birdType;
        this.bounds = new Rectangle(x, y, width, height);
        this.selected = false;
        this.hovered = false;
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        // Card background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (selected) {
            shapeRenderer.setColor(SELECTED_COLOR);
        } else if (hovered) {
            shapeRenderer.setColor(HOVER_COLOR);
        } else {
            shapeRenderer.setColor(CARD_COLOR);
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(selected ? SELECTED_BORDER : BORDER_COLOR);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        // Bird preview area (simple colored rectangle)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Color birdColor = getBirdPreviewColor();
        shapeRenderer.setColor(birdColor);
        float previewSize = bounds.width * 0.4f;
        float previewX = bounds.x + (bounds.width - previewSize) / 2;
        float previewY = bounds.y + bounds.height - previewSize - 15;
        // Simple bird shape
        shapeRenderer.triangle(
            previewX + previewSize / 2, previewY + previewSize,
            previewX, previewY,
            previewX + previewSize, previewY
        );
        shapeRenderer.end();

        // Text info
        batch.begin();
        font.setColor(Color.WHITE);
        float textX = bounds.x + 10;
        float textY = bounds.y + bounds.height * 0.45f;
        float lineHeight = 18;

        // Name
        font.draw(batch, birdType.getDisplayName(), textX, textY);
        textY -= lineHeight;

        // Stats
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, String.format("Speed: %.0f m/s", birdType.getBaseVelocity()), textX, textY);
        textY -= lineHeight;
        font.draw(batch, String.format("Height: %.0f-%.0fm", birdType.getMinHeight(), birdType.getMaxHeight()), textX, textY);
        textY -= lineHeight;
        font.draw(batch, String.format("Cooldown: %.1fs", birdType.getCooldown()), textX, textY);
        textY -= lineHeight;

        // Difficulty
        Color diffColor = getDifficultyColor();
        font.setColor(diffColor);
        font.draw(batch, "Difficulty: " + birdType.getDifficultyLabel(), textX, textY);

        batch.end();
    }

    private Color getBirdPreviewColor() {
        switch (birdType) {
            case SPARROW: return new Color(0.6f, 0.45f, 0.3f, 1f);
            case PIGEON: return new Color(0.5f, 0.5f, 0.55f, 1f);
            case CROW: return new Color(0.15f, 0.15f, 0.2f, 1f);
            case SEAGULL: return new Color(0.95f, 0.95f, 0.95f, 1f);
            case HAWK: return new Color(0.55f, 0.35f, 0.2f, 1f);
            case EAGLE: return new Color(0.25f, 0.2f, 0.15f, 1f);
            default: return Color.GRAY;
        }
    }

    private Color getDifficultyColor() {
        switch (birdType.getDifficulty()) {
            case 1:
            case 2: return Color.GREEN;
            case 3:
            case 4: return Color.YELLOW;
            case 5:
            case 6: return Color.RED;
            default: return Color.WHITE;
        }
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public BirdType getBirdType() {
        return birdType;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }
}
