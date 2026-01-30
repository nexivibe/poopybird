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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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
    private final BitmapFont smallFont;
    private final GlyphLayout layout;

    private final OrthographicCamera camera;
    private final ExtendViewport viewport;

    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    private final Array<BirdCard> birdCards;
    private MenuButton startButton;
    private MenuButton backButton;
    private BirdType selectedBird;

    // Scrolling
    private float scrollOffset;
    private float maxScrollOffset;
    private float targetScrollOffset;
    private boolean needsScrolling;

    // Card layout
    private static final float CARD_WIDTH = 180;
    private static final float CARD_HEIGHT = 240;
    private static final float CARD_SPACING = 20;
    private static final float CARDS_AREA_TOP = VIRTUAL_HEIGHT - 100;
    private static final float CARDS_AREA_BOTTOM = 120;
    private static final int CARDS_PER_ROW = 3;

    public BirdSelectionScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(3.5f);

        this.cardFont = new BitmapFont();
        this.cardFont.getData().setScale(1.2f);

        this.smallFont = new BitmapFont();
        this.smallFont.getData().setScale(1f);

        this.layout = new GlyphLayout();
        this.birdCards = new Array<>();
        this.selectedBird = BirdType.SPARROW;

        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        this.scrollOffset = 0;
        this.targetScrollOffset = 0;

        createCards();
        createButtons();
    }

    private void createCards() {
        birdCards.clear();

        BirdType[] types = BirdType.values();
        int numRows = (int) Math.ceil((double) types.length / CARDS_PER_ROW);

        float totalCardsWidth = CARDS_PER_ROW * CARD_WIDTH + (CARDS_PER_ROW - 1) * CARD_SPACING;
        float startX = (VIRTUAL_WIDTH - totalCardsWidth) / 2;
        float cardsAreaHeight = CARDS_AREA_TOP - CARDS_AREA_BOTTOM;
        float totalCardsHeight = numRows * CARD_HEIGHT + (numRows - 1) * CARD_SPACING;

        // Check if scrolling is needed
        needsScrolling = totalCardsHeight > cardsAreaHeight;
        maxScrollOffset = Math.max(0, totalCardsHeight - cardsAreaHeight);

        for (int i = 0; i < types.length; i++) {
            int row = i / CARDS_PER_ROW;
            int col = i % CARDS_PER_ROW;

            float cardX = startX + col * (CARD_WIDTH + CARD_SPACING);
            // Cards start from top, going down
            float cardY = CARDS_AREA_TOP - CARD_HEIGHT - row * (CARD_HEIGHT + CARD_SPACING);

            BirdCard card = new BirdCard(types[i], cardX, cardY, CARD_WIDTH, CARD_HEIGHT);
            if (types[i] == selectedBird) {
                card.setSelected(true);
            }
            birdCards.add(card);
        }
    }

    private void createButtons() {
        float buttonWidth = 160;
        float buttonHeight = 50;

        startButton = new MenuButton(
            VIRTUAL_WIDTH / 2 + 30,
            40,
            buttonWidth,
            buttonHeight,
            "START",
            cardFont
        );

        backButton = new MenuButton(
            VIRTUAL_WIDTH / 2 - buttonWidth - 30,
            40,
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
        // Smooth scroll
        scrollOffset = MathUtils.lerp(scrollOffset, targetScrollOffset, 10f * delta);

        ScreenUtils.clear(0.1f, 0.12f, 0.18f, 1f);

        viewport.apply();
        camera.update();

        // Convert mouse coordinates
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();
        Vector2 worldCoords = viewport.unproject(new Vector2(mouseX, mouseY));

        // Handle scroll input
        float scrollAmount = -Gdx.input.getDeltaY() * 0.5f;
        if (scrollAmount != 0 && needsScrolling) {
            targetScrollOffset = MathUtils.clamp(targetScrollOffset + scrollAmount, 0, maxScrollOffset);
        }

        // Update hover states (accounting for scroll)
        for (int i = 0; i < birdCards.size; i++) {
            BirdCard card = birdCards.get(i);
            float adjustedY = worldCoords.y - scrollOffset;
            boolean inCardsArea = worldCoords.y < CARDS_AREA_TOP && worldCoords.y > CARDS_AREA_BOTTOM;
            card.setHovered(inCardsArea && card.contains(worldCoords.x, adjustedY + scrollOffset));
        }
        startButton.setHovered(startButton.contains(worldCoords.x, worldCoords.y));
        backButton.setHovered(backButton.contains(worldCoords.x, worldCoords.y));

        // Handle click
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            BirdType clickedBird = null;
            for (int i = 0; i < birdCards.size; i++) {
                BirdCard card = birdCards.get(i);
                float adjustedY = worldCoords.y - scrollOffset;
                boolean inCardsArea = worldCoords.y < CARDS_AREA_TOP && worldCoords.y > CARDS_AREA_BOTTOM;
                if (inCardsArea && card.contains(worldCoords.x, adjustedY + scrollOffset)) {
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectBirdUp();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectBirdDown();
        }

        // Render
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // Draw title
        batch.begin();
        titleFont.setColor(Color.WHITE);
        String title = "Select Your Bird";
        layout.setText(titleFont, title);
        titleFont.draw(batch, title, (VIRTUAL_WIDTH - layout.width) / 2, VIRTUAL_HEIGHT - 30);
        batch.end();

        // Enable scissor for cards area (clipping)
        Gdx.gl.glEnable(Gdx.gl.GL_SCISSOR_TEST);
        int scissorX = (int) viewport.getScreenX();
        int scissorY = (int) (viewport.getScreenY() + CARDS_AREA_BOTTOM * viewport.getScreenHeight() / viewport.getWorldHeight());
        int scissorW = (int) viewport.getScreenWidth();
        int scissorH = (int) ((CARDS_AREA_TOP - CARDS_AREA_BOTTOM) * viewport.getScreenHeight() / viewport.getWorldHeight());
        Gdx.gl.glScissor(scissorX, scissorY, scissorW, scissorH);

        // Draw cards with scroll offset
        for (int i = 0; i < birdCards.size; i++) {
            BirdCard card = birdCards.get(i);
            // Temporarily adjust card position for rendering
            float originalY = card.getBounds().y;
            card.getBounds().y = originalY + scrollOffset;
            card.render(shapeRenderer, batch, cardFont);
            card.getBounds().y = originalY;
        }

        Gdx.gl.glDisable(Gdx.gl.GL_SCISSOR_TEST);

        // Draw scroll indicators if needed
        if (needsScrolling) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.WHITE);

            // Up arrow if can scroll up
            if (targetScrollOffset > 0) {
                float arrowX = VIRTUAL_WIDTH - 30;
                float arrowY = CARDS_AREA_TOP - 20;
                shapeRenderer.triangle(arrowX - 10, arrowY - 10, arrowX + 10, arrowY - 10, arrowX, arrowY + 10);
            }

            // Down arrow if can scroll down
            if (targetScrollOffset < maxScrollOffset) {
                float arrowX = VIRTUAL_WIDTH - 30;
                float arrowY = CARDS_AREA_BOTTOM + 20;
                shapeRenderer.triangle(arrowX - 10, arrowY + 10, arrowX + 10, arrowY + 10, arrowX, arrowY - 10);
            }

            shapeRenderer.end();

            // Scroll bar
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            float scrollBarHeight = CARDS_AREA_TOP - CARDS_AREA_BOTTOM - 60;
            float scrollBarX = VIRTUAL_WIDTH - 15;
            float scrollBarY = CARDS_AREA_BOTTOM + 30;

            // Background
            shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
            shapeRenderer.rect(scrollBarX - 3, scrollBarY, 6, scrollBarHeight);

            // Thumb
            float thumbHeight = Math.max(30, scrollBarHeight * scrollBarHeight / (scrollBarHeight + maxScrollOffset));
            float thumbY = scrollBarY + (scrollBarHeight - thumbHeight) * (1 - targetScrollOffset / maxScrollOffset);
            shapeRenderer.setColor(0.7f, 0.7f, 0.7f, 1f);
            shapeRenderer.rect(scrollBarX - 4, thumbY, 8, thumbHeight);
            shapeRenderer.end();
        }

        // Draw buttons
        startButton.render(shapeRenderer, batch);
        backButton.render(shapeRenderer, batch);

        // Instructions
        batch.begin();
        smallFont.setColor(Color.GRAY);
        String hint = "Arrow keys or click to select | ENTER to start | Scroll for more birds";
        layout.setText(smallFont, hint);
        smallFont.draw(batch, hint, (VIRTUAL_WIDTH - layout.width) / 2, 25);
        batch.end();
    }

    private void selectPreviousBird() {
        BirdType[] types = BirdType.values();
        int currentIndex = selectedBird.ordinal();
        int newIndex = (currentIndex - 1 + types.length) % types.length;
        selectedBird = types[newIndex];
        updateCardSelection();
        ensureSelectedVisible();
    }

    private void selectNextBird() {
        BirdType[] types = BirdType.values();
        int currentIndex = selectedBird.ordinal();
        int newIndex = (currentIndex + 1) % types.length;
        selectedBird = types[newIndex];
        updateCardSelection();
        ensureSelectedVisible();
    }

    private void selectBirdUp() {
        BirdType[] types = BirdType.values();
        int currentIndex = selectedBird.ordinal();
        int newIndex = currentIndex - CARDS_PER_ROW;
        if (newIndex >= 0) {
            selectedBird = types[newIndex];
            updateCardSelection();
            ensureSelectedVisible();
        }
    }

    private void selectBirdDown() {
        BirdType[] types = BirdType.values();
        int currentIndex = selectedBird.ordinal();
        int newIndex = currentIndex + CARDS_PER_ROW;
        if (newIndex < types.length) {
            selectedBird = types[newIndex];
            updateCardSelection();
            ensureSelectedVisible();
        }
    }

    private void ensureSelectedVisible() {
        // Find the selected card and scroll to make it visible
        for (int i = 0; i < birdCards.size; i++) {
            BirdCard card = birdCards.get(i);
            if (card.getBirdType() == selectedBird) {
                float cardTop = card.getBounds().y + card.getBounds().height + scrollOffset;
                float cardBottom = card.getBounds().y + scrollOffset;

                if (cardTop > CARDS_AREA_TOP) {
                    targetScrollOffset -= cardTop - CARDS_AREA_TOP + 10;
                } else if (cardBottom < CARDS_AREA_BOTTOM) {
                    targetScrollOffset += CARDS_AREA_BOTTOM - cardBottom + 10;
                }
                targetScrollOffset = MathUtils.clamp(targetScrollOffset, 0, maxScrollOffset);
                break;
            }
        }
    }

    private void updateCardSelection() {
        for (int i = 0; i < birdCards.size; i++) {
            BirdCard card = birdCards.get(i);
            card.setSelected(card.getBirdType() == selectedBird);
        }
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
        cardFont.dispose();
        smallFont.dispose();
    }
}
