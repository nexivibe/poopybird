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
import ape.poopybird.ui.MenuButton;

public class MainMenuScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;
    private final GlyphLayout layout;

    private MenuButton playButton;
    private MenuButton quitButton;

    public MainMenuScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(4f);

        this.buttonFont = new BitmapFont();
        this.buttonFont.getData().setScale(2f);

        this.layout = new GlyphLayout();

        createButtons();
    }

    private void createButtons() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float buttonWidth = 200;
        float buttonHeight = 50;
        float centerX = (screenWidth - buttonWidth) / 2;

        playButton = new MenuButton(centerX, screenHeight / 2, buttonWidth, buttonHeight, "PLAY", buttonFont);
        quitButton = new MenuButton(centerX, screenHeight / 2 - 70, buttonWidth, buttonHeight, "QUIT", buttonFont);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.15f, 0.2f, 1f);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Handle input
        float mouseX = Gdx.input.getX();
        float mouseY = screenHeight - Gdx.input.getY(); // Flip Y

        playButton.setHovered(playButton.contains(mouseX, mouseY));
        quitButton.setHovered(quitButton.contains(mouseX, mouseY));

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (playButton.isHovered()) {
                game.setScreen(new BirdSelectionScreen(game));
                return;
            } else if (quitButton.isHovered()) {
                Gdx.app.exit();
                return;
            }
        }

        // Keyboard shortcuts
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new BirdSelectionScreen(game));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
            return;
        }

        // Draw title
        batch.begin();
        titleFont.setColor(Color.WHITE);
        String title = "PoopyBird";
        layout.setText(titleFont, title);
        titleFont.draw(batch, title, (screenWidth - layout.width) / 2, screenHeight - 80);

        // Instructions
        buttonFont.setColor(Color.LIGHT_GRAY);
        String instructions = "Drop poop on targets for points!";
        layout.setText(buttonFont, instructions);
        buttonFont.draw(batch, instructions, (screenWidth - layout.width) / 2, screenHeight - 150);
        batch.end();

        // Draw buttons
        playButton.render(shapeRenderer, batch);
        quitButton.render(shapeRenderer, batch);

        // Controls info at bottom
        batch.begin();
        buttonFont.setColor(Color.GRAY);
        buttonFont.getData().setScale(1f);
        String controls = "Controls: WASD/Mouse to move | SPACE/Click to poop";
        layout.setText(buttonFont, controls);
        buttonFont.draw(batch, controls, (screenWidth - layout.width) / 2, 40);
        buttonFont.getData().setScale(2f);
        batch.end();
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
        buttonFont.dispose();
    }
}
