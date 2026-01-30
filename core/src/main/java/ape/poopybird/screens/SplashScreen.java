package ape.poopybird.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import ape.poopybird.Main;
import ape.poopybird.util.Constants;

public class SplashScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont subtitleFont;
    private final GlyphLayout layout;
    private float elapsedTime;

    private final OrthographicCamera camera;
    private final ExtendViewport viewport;

    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    public SplashScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();

        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(5f);
        this.titleFont.setColor(Color.WHITE);

        this.subtitleFont = new BitmapFont();
        this.subtitleFont.getData().setScale(2f);
        this.subtitleFont.setColor(Color.LIGHT_GRAY);

        this.layout = new GlyphLayout();
        this.elapsedTime = 0;

        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    }

    @Override
    public void show() {
        elapsedTime = 0;
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;

        // Transition after splash duration
        if (elapsedTime >= Constants.SPLASH_DURATION) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        // Fade in effect
        float alpha = Math.min(1f, elapsedTime / 0.5f);

        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Title
        titleFont.setColor(1f, 1f, 1f, alpha);
        String title = "PoopyBird";
        layout.setText(titleFont, title);
        titleFont.draw(batch, title,
            (VIRTUAL_WIDTH - layout.width) / 2,
            VIRTUAL_HEIGHT / 2 + layout.height);

        // Subtitle
        subtitleFont.setColor(0.7f, 0.7f, 0.7f, alpha);
        String subtitle = "The Ultimate Aerial Bombardment Simulator";
        layout.setText(subtitleFont, subtitle);
        subtitleFont.draw(batch, subtitle,
            (VIRTUAL_WIDTH - layout.width) / 2,
            VIRTUAL_HEIGHT / 2 - 40);

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
        titleFont.dispose();
        subtitleFont.dispose();
    }
}
