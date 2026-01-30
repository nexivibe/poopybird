package ape.poopybird.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import ape.poopybird.Main;
import ape.poopybird.entities.BirdType;
import ape.poopybird.ui.BirdCard;
import ape.poopybird.ui.MenuButton;

public class BirdSelectionScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont titleFont;
    private final BitmapFont cardFont;
    private final GlyphLayout layout;

    private final Array<BirdCard> birdCards;
    private MenuButton startButton;
    private MenuButton backButton;
    private BirdType selectedBird;

    private static final float CARD_WIDTH = 160;
    private static final float CARD_HEIGHT = 200;
    private static final float CARD_SPACING = 20;

    public BirdSelectionScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(3f);

        this.cardFont = new BitmapFont();
        this.cardFont.getData().setScale(1f);

        this.layout = new GlyphLayout();
        this.birdCards = new Array<>();
        this.selectedBird = BirdType.SPARROW;

        createCards();
        createButtons();
    }

    private void createCards() {
        birdCards.clear();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        BirdType[] types = BirdType.values();
        float totalWidth = types.length * CARD_WIDTH + (types.length - 1) * CARD_SPACING;
        float startX = (screenWidth - totalWidth) / 2;
        float cardY = screenHeight / 2 - CARD_HEIGHT / 2;

        for (int i = 0; i < types.length; i++) {
            float cardX = startX + i * (CARD_WIDTH + CARD_SPACING);
            BirdCard card = new BirdCard(types[i], cardX, cardY, CARD_WIDTH, CARD_HEIGHT);
            if (types[i] == selectedBird) {
                card.setSelected(true);
            }
            birdCards.add(card);
        }
    }

    private void createButtons() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float buttonWidth = 150;
        float buttonHeight = 40;

        startButton = new MenuButton(
            screenWidth / 2 + 20,
            50,
            buttonWidth,
            buttonHeight,
            "START",
            cardFont
        );

        backButton = new MenuButton(
            screenWidth / 2 - buttonWidth - 20,
            50,
            buttonWidth,
            buttonHeight,
            "BACK",
            cardFont
        );
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.12f, 0.18f, 1f);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float mouseX = Gdx.input.getX();
        float mouseY = screenHeight - Gdx.input.getY();

        // Update hover states
        for (int i = 0; i < birdCards.size; i++) {
            BirdCard card = birdCards.get(i);
            card.setHovered(card.contains(mouseX, mouseY));
        }
        startButton.setHovered(startButton.contains(mouseX, mouseY));
        backButton.setHovered(backButton.contains(mouseX, mouseY));

        // Handle click
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            BirdType clickedBird = null;
            for (int i = 0; i < birdCards.size; i++) {
                BirdCard card = birdCards.get(i);
                if (card.contains(mouseX, mouseY)) {
                    clickedBird = card.getBirdType();
                    break;
                }
            }
            if (clickedBird != null) {
                selectedBird = clickedBird;
                updateCardSelection();
            }

            if (startButton.isHovered()) {
                game.setScreen(new GameScreen(game, selectedBird));
                return;
            }
            if (backButton.isHovered()) {
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        }

        // Keyboard navigation
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game, selectedBird));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectPreviousBird();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectNextBird();
        }

        // Draw title
        batch.begin();
        titleFont.setColor(Color.WHITE);
        String title = "Select Your Bird";
        layout.setText(titleFont, title);
        titleFont.draw(batch, title, (screenWidth - layout.width) / 2, screenHeight - 40);
        batch.end();

        // Draw cards
        for (int i = 0; i < birdCards.size; i++) {
            birdCards.get(i).render(shapeRenderer, batch, cardFont);
        }

        // Draw buttons
        startButton.render(shapeRenderer, batch);
        backButton.render(shapeRenderer, batch);

        // Instructions
        batch.begin();
        cardFont.setColor(Color.GRAY);
        String hint = "Use arrow keys or click to select. Press ENTER to start.";
        layout.setText(cardFont, hint);
        cardFont.draw(batch, hint, (screenWidth - layout.width) / 2, 130);
        batch.end();
    }

    private void selectPreviousBird() {
        BirdType[] types = BirdType.values();
        int currentIndex = selectedBird.ordinal();
        int newIndex = (currentIndex - 1 + types.length) % types.length;
        selectedBird = types[newIndex];
        updateCardSelection();
    }

    private void selectNextBird() {
        BirdType[] types = BirdType.values();
        int currentIndex = selectedBird.ordinal();
        int newIndex = (currentIndex + 1) % types.length;
        selectedBird = types[newIndex];
        updateCardSelection();
    }

    private void updateCardSelection() {
        for (int i = 0; i < birdCards.size; i++) {
            BirdCard card = birdCards.get(i);
            card.setSelected(card.getBirdType() == selectedBird);
        }
    }

    @Override
    public void resize(int width, int height) {
        createCards();
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
        cardFont.dispose();
    }
}
