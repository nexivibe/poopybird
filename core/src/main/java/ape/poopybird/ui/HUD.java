package ape.poopybird.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ape.poopybird.entities.Bird;
import ape.poopybird.physics.Wind;
import ape.poopybird.util.GameState;

public class HUD {
    private final BitmapFont font;
    private final BitmapFont largeFont;
    private final GlyphLayout layout;
    private final ShapeRenderer shapeRenderer;

    private static final float PADDING = 10f;
    private static final float COOLDOWN_BAR_WIDTH = 200f;
    private static final float COOLDOWN_BAR_HEIGHT = 20f;

    // Hit feedback
    private String hitMessage;
    private int hitScore;
    private float hitDisplayTime;
    private static final float HIT_DISPLAY_DURATION = 1.5f;

    public HUD() {
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.5f);

        this.largeFont = new BitmapFont();
        this.largeFont.setColor(Color.YELLOW);
        this.largeFont.getData().setScale(3f);

        this.layout = new GlyphLayout();
        this.shapeRenderer = new ShapeRenderer();

        this.hitMessage = "";
        this.hitScore = 0;
        this.hitDisplayTime = 0;
    }

    public void update(float delta) {
        if (hitDisplayTime > 0) {
            hitDisplayTime -= delta;
        }
    }

    public void showHit(String zone, int score) {
        this.hitMessage = zone;
        this.hitScore = score;
        this.hitDisplayTime = HIT_DISPLAY_DURATION;
    }

    public void render(SpriteBatch batch, GameState gameState, Bird bird, Wind wind) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        batch.begin();

        // Score (top-left)
        font.setColor(Color.WHITE);
        font.draw(batch, "Score: " + gameState.getScore(), PADDING, screenHeight - PADDING);

        // Time remaining (top-center)
        int seconds = (int) gameState.getTimeRemaining();
        String timeText = String.format("Time: %d:%02d", seconds / 60, seconds % 60);
        layout.setText(font, timeText);
        font.draw(batch, timeText, (screenWidth - layout.width) / 2, screenHeight - PADDING);

        // Height (top-right)
        String heightText = String.format("Height: %.0fm", bird.getHeight());
        layout.setText(font, heightText);
        font.draw(batch, heightText, screenWidth - layout.width - PADDING, screenHeight - PADDING);

        // Wind (bottom-left)
        String windText = String.format("Wind: %.1f m/s %s", wind.getSpeed(), wind.getDirectionName());
        font.draw(batch, windText, PADDING, PADDING + 60);

        // Hit feedback (center)
        if (hitDisplayTime > 0) {
            float alpha = Math.min(1f, hitDisplayTime / 0.5f);
            largeFont.setColor(1f, 1f, 0f, alpha);
            String feedbackText = hitMessage + " +" + hitScore;
            layout.setText(largeFont, feedbackText);
            largeFont.draw(batch, feedbackText,
                (screenWidth - layout.width) / 2,
                screenHeight / 2 + 50);
        }

        batch.end();

        // Cooldown bar (bottom-center) - using ShapeRenderer
        renderCooldownBar(bird.getCooldownProgress(), screenWidth, screenHeight);
    }

    private void renderCooldownBar(float progress, float screenWidth, float screenHeight) {
        float barX = (screenWidth - COOLDOWN_BAR_WIDTH) / 2;
        float barY = PADDING;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.8f);
        shapeRenderer.rect(barX, barY, COOLDOWN_BAR_WIDTH, COOLDOWN_BAR_HEIGHT);

        // Progress
        if (progress >= 1f) {
            shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1f);
        } else {
            shapeRenderer.setColor(0.8f, 0.6f, 0.2f, 1f);
        }
        shapeRenderer.rect(barX, barY, COOLDOWN_BAR_WIDTH * progress, COOLDOWN_BAR_HEIGHT);

        shapeRenderer.end();

        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(barX, barY, COOLDOWN_BAR_WIDTH, COOLDOWN_BAR_HEIGHT);
        shapeRenderer.end();
    }

    public void renderWindArrow(float screenX, float screenY, float angle, float speed) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float arrowLength = 20 + speed * 5;
        float radians = (float) Math.toRadians(angle);

        float endX = screenX + (float) Math.sin(radians) * arrowLength;
        float endY = screenY + (float) Math.cos(radians) * arrowLength;

        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rectLine(screenX, screenY, endX, endY, 3);

        // Arrow head
        float headSize = 8;
        float headAngle1 = radians + (float) Math.PI * 0.8f;
        float headAngle2 = radians - (float) Math.PI * 0.8f;

        shapeRenderer.triangle(
            endX, endY,
            endX + (float) Math.sin(headAngle1) * headSize, endY + (float) Math.cos(headAngle1) * headSize,
            endX + (float) Math.sin(headAngle2) * headSize, endY + (float) Math.cos(headAngle2) * headSize
        );

        shapeRenderer.end();
    }

    public void dispose() {
        font.dispose();
        largeFont.dispose();
        shapeRenderer.dispose();
    }
}
