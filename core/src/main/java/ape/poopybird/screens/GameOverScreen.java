package ape.poopybird.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import ape.poopybird.Main;
import ape.poopybird.entities.BirdType;
import ape.poopybird.ui.MenuButton;
import ape.poopybird.util.GameState;

public class GameOverScreen implements Screen {
    private final Main game;
    private final GameState gameState;
    private final BirdType birdType;

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont titleFont;
    private final BitmapFont scoreFont;
    private final BitmapFont buttonFont;
    private final GlyphLayout layout;

    private MenuButton replayButton;
    private MenuButton selectBirdButton;
    private MenuButton menuButton;

    public GameOverScreen(Main game, GameState gameState, BirdType birdType) {
        this.game = game;
        this.gameState = gameState;
        this.birdType = birdType;

        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(4f);

        this.scoreFont = new BitmapFont();
        this.scoreFont.getData().setScale(2f);

        this.buttonFont = new BitmapFont();
        this.buttonFont.getData().setScale(1.5f);

        this.layout = new GlyphLayout();

        createButtons();
    }

    private void createButtons() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float buttonWidth = 180;
        float buttonHeight = 45;
        float buttonY = screenHeight * 0.25f;
        float spacing = 20;
        float totalWidth = 3 * buttonWidth + 2 * spacing;
        float startX = (screenWidth - totalWidth) / 2;

        replayButton = new MenuButton(startX, buttonY, buttonWidth, buttonHeight, "PLAY AGAIN", buttonFont);
        selectBirdButton = new MenuButton(startX + buttonWidth + spacing, buttonY, buttonWidth, buttonHeight, "SELECT BIRD", buttonFont);
        menuButton = new MenuButton(startX + 2 * (buttonWidth + spacing), buttonY, buttonWidth, buttonHeight, "MAIN MENU", buttonFont);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float mouseX = Gdx.input.getX();
        float mouseY = screenHeight - Gdx.input.getY();

        // Update button hover states
        replayButton.setHovered(replayButton.contains(mouseX, mouseY));
        selectBirdButton.setHovered(selectBirdButton.contains(mouseX, mouseY));
        menuButton.setHovered(menuButton.contains(mouseX, mouseY));

        // Handle clicks
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (replayButton.isHovered()) {
                game.setScreen(new GameScreen(game, birdType));
                return;
            }
            if (selectBirdButton.isHovered()) {
                game.setScreen(new BirdSelectionScreen(game));
                return;
            }
            if (menuButton.isHovered()) {
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        }

        // Keyboard shortcuts
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.setScreen(new GameScreen(game, birdType));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game, birdType));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        // Draw content
        batch.begin();

        // Game Over title
        titleFont.setColor(Color.WHITE);
        String title = "GAME OVER";
        layout.setText(titleFont, title);
        titleFont.draw(batch, title, (screenWidth - layout.width) / 2, screenHeight - 60);

        // Final score
        scoreFont.setColor(Color.YELLOW);
        String scoreText = "Final Score: " + gameState.getScore();
        layout.setText(scoreFont, scoreText);
        scoreFont.draw(batch, scoreText, (screenWidth - layout.width) / 2, screenHeight - 140);

        // Stats
        scoreFont.setColor(Color.WHITE);
        float statsY = screenHeight - 200;
        float lineHeight = 35;

        String birdText = "Bird: " + birdType.getDisplayName();
        layout.setText(scoreFont, birdText);
        scoreFont.draw(batch, birdText, (screenWidth - layout.width) / 2, statsY);

        String poopsText = "Poops Dropped: " + gameState.getPoopCount();
        layout.setText(scoreFont, poopsText);
        scoreFont.draw(batch, poopsText, (screenWidth - layout.width) / 2, statsY - lineHeight);

        String hitsText = "Targets Hit: " + gameState.getHitCount();
        layout.setText(scoreFont, hitsText);
        scoreFont.draw(batch, hitsText, (screenWidth - layout.width) / 2, statsY - lineHeight * 2);

        String accuracyText = String.format("Accuracy: %.1f%%", gameState.getAccuracy());
        layout.setText(scoreFont, accuracyText);
        scoreFont.draw(batch, accuracyText, (screenWidth - layout.width) / 2, statsY - lineHeight * 3);

        // Rating based on score
        scoreFont.setColor(getRatingColor());
        String rating = getRating();
        layout.setText(scoreFont, rating);
        scoreFont.draw(batch, rating, (screenWidth - layout.width) / 2, statsY - lineHeight * 4.5f);

        batch.end();

        // Draw buttons
        replayButton.render(shapeRenderer, batch);
        selectBirdButton.render(shapeRenderer, batch);
        menuButton.render(shapeRenderer, batch);

        // Hint
        batch.begin();
        buttonFont.setColor(Color.GRAY);
        String hint = "Press R or ENTER to play again, ESC for menu";
        layout.setText(buttonFont, hint);
        buttonFont.draw(batch, hint, (screenWidth - layout.width) / 2, 40);
        batch.end();
    }

    private String getRating() {
        int score = gameState.getScore();
        if (score >= 2000) return "LEGENDARY POOPER!";
        if (score >= 1500) return "Master Bomber!";
        if (score >= 1000) return "Expert Dropper!";
        if (score >= 500) return "Skilled Pooper";
        if (score >= 200) return "Decent Aim";
        if (score >= 50) return "Beginner Bomber";
        return "Keep Practicing!";
    }

    private Color getRatingColor() {
        int score = gameState.getScore();
        if (score >= 2000) return Color.GOLD;
        if (score >= 1500) return Color.PURPLE;
        if (score >= 1000) return Color.CYAN;
        if (score >= 500) return Color.GREEN;
        if (score >= 200) return Color.YELLOW;
        return Color.LIGHT_GRAY;
    }

    @Override
    public void resize(int width, int height) {
        createButtons();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        titleFont.dispose();
        scoreFont.dispose();
        buttonFont.dispose();
    }
}
