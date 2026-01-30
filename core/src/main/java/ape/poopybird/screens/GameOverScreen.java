package ape.poopybird.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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
    private final BitmapFont smallFont;
    private final GlyphLayout layout;

    private final OrthographicCamera camera;
    private final ExtendViewport viewport;

    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

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
        this.titleFont.getData().setScale(5f);

        this.scoreFont = new BitmapFont();
        this.scoreFont.getData().setScale(2.5f);

        this.buttonFont = new BitmapFont();
        this.buttonFont.getData().setScale(1.8f);

        this.smallFont = new BitmapFont();
        this.smallFont.getData().setScale(1.2f);

        this.layout = new GlyphLayout();

        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        createButtons();
    }

    private void createButtons() {
        float buttonWidth = 200;
        float buttonHeight = 55;
        float buttonY = 80;
        float spacing = 25;
        float totalWidth = 3 * buttonWidth + 2 * spacing;
        float startX = (VIRTUAL_WIDTH - totalWidth) / 2;

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

        viewport.apply();
        camera.update();

        // Convert mouse coordinates
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();
        Vector2 worldCoords = viewport.unproject(new Vector2(mouseX, mouseY));

        // Update button hover states
        replayButton.setHovered(replayButton.contains(worldCoords.x, worldCoords.y));
        selectBirdButton.setHovered(selectBirdButton.contains(worldCoords.x, worldCoords.y));
        menuButton.setHovered(menuButton.contains(worldCoords.x, worldCoords.y));

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

        // Set projection matrices
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // Draw content
        batch.begin();

        // Game Over title
        titleFont.setColor(Color.WHITE);
        String title = "GAME OVER";
        layout.setText(titleFont, title);
        titleFont.draw(batch, title, (VIRTUAL_WIDTH - layout.width) / 2, VIRTUAL_HEIGHT - 50);

        // Final score
        scoreFont.setColor(Color.YELLOW);
        String scoreText = "Final Score: " + gameState.getScore();
        layout.setText(scoreFont, scoreText);
        scoreFont.draw(batch, scoreText, (VIRTUAL_WIDTH - layout.width) / 2, VIRTUAL_HEIGHT - 130);

        // Stats
        scoreFont.setColor(Color.WHITE);
        float statsY = VIRTUAL_HEIGHT - 200;
        float lineHeight = 40;

        String birdText = "Bird: " + birdType.getDisplayName();
        layout.setText(scoreFont, birdText);
        scoreFont.draw(batch, birdText, (VIRTUAL_WIDTH - layout.width) / 2, statsY);

        String poopsText = "Poops Dropped: " + gameState.getPoopCount();
        layout.setText(scoreFont, poopsText);
        scoreFont.draw(batch, poopsText, (VIRTUAL_WIDTH - layout.width) / 2, statsY - lineHeight);

        String hitsText = "Targets Hit: " + gameState.getHitCount();
        layout.setText(scoreFont, hitsText);
        scoreFont.draw(batch, hitsText, (VIRTUAL_WIDTH - layout.width) / 2, statsY - lineHeight * 2);

        String accuracyText = String.format("Accuracy: %.1f%%", gameState.getAccuracy());
        layout.setText(scoreFont, accuracyText);
        scoreFont.draw(batch, accuracyText, (VIRTUAL_WIDTH - layout.width) / 2, statsY - lineHeight * 3);

        // Rating based on score
        scoreFont.setColor(getRatingColor());
        String rating = getRating();
        layout.setText(scoreFont, rating);
        scoreFont.draw(batch, rating, (VIRTUAL_WIDTH - layout.width) / 2, statsY - lineHeight * 4.5f);

        batch.end();

        // Draw buttons
        replayButton.render(shapeRenderer, batch);
        selectBirdButton.render(shapeRenderer, batch);
        menuButton.render(shapeRenderer, batch);

        // Hint
        batch.begin();
        smallFont.setColor(Color.GRAY);
        String hint = "Press R or ENTER to play again, ESC for menu";
        layout.setText(smallFont, hint);
        smallFont.draw(batch, hint, (VIRTUAL_WIDTH - layout.width) / 2, 40);
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
        viewport.update(width, height, true);
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
        smallFont.dispose();
    }
}
