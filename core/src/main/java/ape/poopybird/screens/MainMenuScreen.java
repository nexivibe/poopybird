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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import ape.poopybird.Main;
import ape.poopybird.ui.MenuButton;

public class MainMenuScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;
    private final BitmapFont smallFont;
    private final GlyphLayout layout;

    private final OrthographicCamera camera;
    private final ExtendViewport viewport;

    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    private MenuButton playButton;
    private MenuButton quitButton;

    public MainMenuScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(5f);

        this.buttonFont = new BitmapFont();
        this.buttonFont.getData().setScale(2.5f);

        this.smallFont = new BitmapFont();
        this.smallFont.getData().setScale(1.5f);

        this.layout = new GlyphLayout();

        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        createButtons();
    }

    private void createButtons() {
        float buttonWidth = 250;
        float buttonHeight = 60;
        float centerX = (VIRTUAL_WIDTH - buttonWidth) / 2;

        playButton = new MenuButton(centerX, VIRTUAL_HEIGHT / 2 - 20, buttonWidth, buttonHeight, "PLAY", buttonFont);
        quitButton = new MenuButton(centerX, VIRTUAL_HEIGHT / 2 - 100, buttonWidth, buttonHeight, "QUIT", buttonFont);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.15f, 0.2f, 1f);

        viewport.apply();
        camera.update();

        // Convert mouse coordinates to world coordinates
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();
        com.badlogic.gdx.math.Vector2 worldCoords = viewport.unproject(new com.badlogic.gdx.math.Vector2(mouseX, mouseY));

        playButton.setHovered(playButton.contains(worldCoords.x, worldCoords.y));
        quitButton.setHovered(quitButton.contains(worldCoords.x, worldCoords.y));

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

        // Render with camera
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // Draw title
        batch.begin();
        titleFont.setColor(Color.WHITE);
        String title = "PoopyBird";
        layout.setText(titleFont, title);
        titleFont.draw(batch, title, (VIRTUAL_WIDTH - layout.width) / 2, VIRTUAL_HEIGHT - 60);

        // Tagline
        buttonFont.setColor(new Color(0.8f, 0.9f, 0.8f, 1f));
        String tagline = "Bombs Away!";
        layout.setText(buttonFont, tagline);
        buttonFont.draw(batch, tagline, (VIRTUAL_WIDTH - layout.width) / 2, VIRTUAL_HEIGHT - 140);
        batch.end();

        // Draw buttons
        playButton.render(shapeRenderer, batch);
        quitButton.render(shapeRenderer, batch);

        // Controls info at bottom
        batch.begin();
        smallFont.setColor(Color.GRAY);
        String controls = "Controls: WASD or Arrow Keys to fly | SPACE or Click to poop";
        layout.setText(smallFont, controls);
        smallFont.draw(batch, controls, (VIRTUAL_WIDTH - layout.width) / 2, 50);
        batch.end();
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
        buttonFont.dispose();
        smallFont.dispose();
    }
}
