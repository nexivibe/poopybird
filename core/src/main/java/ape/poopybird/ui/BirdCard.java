package ape.poopybird.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import ape.poopybird.entities.BirdType;

public class BirdCard {
    private final BirdType birdType;
    private final Rectangle bounds;
    private boolean selected;
    private boolean hovered;
    private final GlyphLayout layout;

    private static final Color CARD_COLOR = new Color(0.2f, 0.2f, 0.28f, 1f);
    private static final Color SELECTED_COLOR = new Color(0.25f, 0.45f, 0.25f, 1f);
    private static final Color HOVER_COLOR = new Color(0.3f, 0.3f, 0.38f, 1f);
    private static final Color BORDER_COLOR = new Color(0.4f, 0.4f, 0.5f, 1f);
    private static final Color SELECTED_BORDER = new Color(0.4f, 0.9f, 0.4f, 1f);

    public BirdCard(BirdType birdType, float x, float y, float width, float height) {
        this.birdType = birdType;
        this.bounds = new Rectangle(x, y, width, height);
        this.selected = false;
        this.hovered = false;
        this.layout = new GlyphLayout();
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        float padding = 12f;

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
        if (selected) {
            // Double border for selected
            shapeRenderer.rect(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4);
        }
        shapeRenderer.end();

        // Bird preview area (simple colored triangle bird)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Color birdColor = getBirdPreviewColor();
        shapeRenderer.setColor(birdColor);
        float previewSize = bounds.width * 0.35f;
        float previewX = bounds.x + (bounds.width - previewSize) / 2;
        float previewY = bounds.y + bounds.height - previewSize - 25;

        // Bird body (triangle pointing right)
        shapeRenderer.triangle(
            previewX, previewY + previewSize * 0.3f,
            previewX, previewY + previewSize * 0.7f,
            previewX + previewSize, previewY + previewSize * 0.5f
        );

        // Wing
        Color wingColor = birdColor.cpy().mul(0.7f);
        shapeRenderer.setColor(wingColor);
        shapeRenderer.triangle(
            previewX + previewSize * 0.2f, previewY + previewSize * 0.5f,
            previewX + previewSize * 0.5f, previewY + previewSize * 0.9f,
            previewX + previewSize * 0.5f, previewY + previewSize * 0.5f
        );
        shapeRenderer.end();

        // Text info
        batch.begin();

        // Name (centered, larger)
        font.setColor(Color.WHITE);
        font.getData().setScale(1.4f);
        layout.setText(font, birdType.getDisplayName());
        font.draw(batch, birdType.getDisplayName(),
            bounds.x + (bounds.width - layout.width) / 2,
            bounds.y + bounds.height * 0.42f);

        // Stats (smaller, left-aligned with padding)
        font.getData().setScale(1f);
        font.setColor(new Color(0.8f, 0.8f, 0.85f, 1f));

        float statsX = bounds.x + padding;
        float statsY = bounds.y + bounds.height * 0.32f;
        float lineHeight = 22f;

        font.draw(batch, String.format("Speed: %.0f", birdType.getBaseVelocity()), statsX, statsY);
        statsY -= lineHeight;

        font.draw(batch, String.format("Height: %.0f-%.0f", birdType.getMinHeight(), birdType.getMaxHeight()), statsX, statsY);
        statsY -= lineHeight;

        font.draw(batch, String.format("Cooldown: %.1fs", birdType.getCooldown()), statsX, statsY);
        statsY -= lineHeight + 5;

        // Difficulty with color
        Color diffColor = getDifficultyColor();
        font.setColor(diffColor);
        String diffText = birdType.getDifficultyLabel();
        layout.setText(font, diffText);
        font.draw(batch, diffText, bounds.x + (bounds.width - layout.width) / 2, statsY);

        // Reset font scale
        font.getData().setScale(1.2f);
        batch.end();
    }

    private Color getBirdPreviewColor() {
        switch (birdType) {
            case SPARROW: return new Color(0.65f, 0.5f, 0.35f, 1f);
            case PIGEON: return new Color(0.55f, 0.55f, 0.6f, 1f);
            case CROW: return new Color(0.2f, 0.2f, 0.25f, 1f);
            case SEAGULL: return new Color(0.95f, 0.95f, 0.95f, 1f);
            case HAWK: return new Color(0.6f, 0.4f, 0.25f, 1f);
            case EAGLE: return new Color(0.35f, 0.28f, 0.2f, 1f);
            default: return Color.GRAY;
        }
    }

    private Color getDifficultyColor() {
        switch (birdType.getDifficulty()) {
            case 1: return new Color(0.3f, 0.9f, 0.3f, 1f);  // Easy - bright green
            case 2: return new Color(0.5f, 0.9f, 0.3f, 1f);  // Easy+ - yellow-green
            case 3: return new Color(0.9f, 0.9f, 0.3f, 1f);  // Medium - yellow
            case 4: return new Color(0.9f, 0.6f, 0.3f, 1f);  // Medium+ - orange
            case 5: return new Color(0.9f, 0.4f, 0.3f, 1f);  // Hard - red-orange
            case 6: return new Color(0.9f, 0.2f, 0.2f, 1f);  // Expert - red
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
