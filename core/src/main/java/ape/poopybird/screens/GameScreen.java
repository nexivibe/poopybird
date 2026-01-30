package ape.poopybird.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import ape.poopybird.Main;
import ape.poopybird.entities.*;
import ape.poopybird.graphics.*;
import ape.poopybird.input.GameInputProcessor;
import ape.poopybird.physics.PhysicsEngine;
import ape.poopybird.physics.ProjectileCalculator;
import ape.poopybird.util.Constants;
import ape.poopybird.util.GameState;

public class GameScreen implements Screen {
    private final Main game;
    private final BirdType birdType;

    // 3D rendering
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;
    private ModelBuilder modelBuilder;

    // Models
    private Model groundModel;
    private Model birdModel;
    private Model poopModel;
    private final Array<Model> targetModels;
    private Model powerUpModel;
    private Model targetMarkerModel;

    // Instances
    private ModelInstance groundInstance;
    private ModelInstance birdInstance;
    private ModelInstance targetMarkerInstance;

    // Game objects
    private Bird bird;
    private PhysicsEngine physics;
    private GameState gameState;
    private GameInputProcessor inputProcessor;

    // UI
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont bigFont;
    private GlyphLayout layout;

    // Target spawning
    private float targetSpawnTimer;
    private float powerUpSpawnTimer;

    // Camera smoothing
    private float currentCameraAngle;
    private Vector3 cameraOffset;
    private static final float CAMERA_DISTANCE = 25f;
    private static final float CAMERA_HEIGHT = 12f;
    private static final float CAMERA_SMOOTH = 5f;

    // Visual feedback
    private String hitMessage;
    private int hitScore;
    private float hitDisplayTime;
    private static final float HIT_DISPLAY_DURATION = 1.5f;

    // Landing prediction
    private Vector3 predictedLanding;

    // Scale multiplier for models
    private static final float MODEL_SCALE = 3f;

    public GameScreen(Main game, BirdType birdType) {
        this.game = game;
        this.birdType = birdType;
        this.targetModels = new Array<>();
        this.cameraOffset = new Vector3();
        this.predictedLanding = new Vector3();
        this.hitMessage = "";
    }

    @Override
    public void show() {
        // Initialize rendering
        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        font = new BitmapFont();
        font.getData().setScale(2f);

        bigFont = new BitmapFont();
        bigFont.getData().setScale(4f);

        layout = new GlyphLayout();

        // Setup camera (third-person behind bird)
        camera = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 1f;
        camera.far = 500f;
        currentCameraAngle = 0;

        // Setup lighting - brighter and more cheerful
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        environment.add(new DirectionalLight().set(1f, 0.95f, 0.8f, -0.3f, -1f, -0.5f));

        // Create models
        createModels();

        // Initialize game state
        gameState = new GameState();
        physics = new PhysicsEngine(gameState);

        // Create bird
        bird = new Bird(birdType);
        birdInstance = new ModelInstance(birdModel);
        bird.setModelInstance(birdInstance);

        // Create ground
        groundInstance = new ModelInstance(groundModel);

        // Create target marker (shows where poop will land)
        targetMarkerInstance = new ModelInstance(targetMarkerModel);

        // Setup input
        inputProcessor = new GameInputProcessor();
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(inputProcessor);
        Gdx.input.setInputProcessor(multiplexer);

        // Spawn initial targets
        spawnInitialTargets();

        // Reset timers
        targetSpawnTimer = 0;
        powerUpSpawnTimer = 0;
    }

    private void createModels() {
        // Ground - much larger and more colorful terrain
        groundModel = createTerrain();

        // Bird - scaled up
        birdModel = OrigamiBirdBuilder.buildBird(modelBuilder, birdType);

        // Poop - scaled up
        poopModel = PoopModelBuilder.buildPoop(modelBuilder);

        // Targets - all scaled up
        for (TargetType type : TargetType.values()) {
            targetModels.add(TargetModelBuilder.buildTarget(modelBuilder, type));
        }

        // Power-up
        powerUpModel = PoopModelBuilder.buildPowerUp(modelBuilder);

        // Target marker (landing prediction circle)
        targetMarkerModel = createTargetMarker();
    }

    private Model createTerrain() {
        modelBuilder.begin();

        // Main grass - bright green
        com.badlogic.gdx.graphics.g3d.Material grassMaterial = new com.badlogic.gdx.graphics.g3d.Material(
            ColorAttribute.createDiffuse(new Color(0.35f, 0.7f, 0.25f, 1f)));
        com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder grassBuilder = modelBuilder.part("grass",
            GL20.GL_TRIANGLES,
            com.badlogic.gdx.graphics.VertexAttributes.Usage.Position | com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal,
            grassMaterial);

        float size = Constants.WORLD_WIDTH;
        grassBuilder.rect(
            new Vector3(-size, 0, -size),
            new Vector3(size, 0, -size),
            new Vector3(size, 0, size),
            new Vector3(-size, 0, size),
            new Vector3(0, 1, 0)
        );

        // Paths - tan color crossing the park
        com.badlogic.gdx.graphics.g3d.Material pathMaterial = new com.badlogic.gdx.graphics.g3d.Material(
            ColorAttribute.createDiffuse(new Color(0.7f, 0.6f, 0.45f, 1f)));
        com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder pathBuilder = modelBuilder.part("paths",
            GL20.GL_TRIANGLES,
            com.badlogic.gdx.graphics.VertexAttributes.Usage.Position | com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal,
            pathMaterial);

        float pathWidth = 8f;
        float pathY = 0.05f;

        // Main horizontal path
        pathBuilder.rect(
            new Vector3(-size, pathY, -pathWidth / 2),
            new Vector3(size, pathY, -pathWidth / 2),
            new Vector3(size, pathY, pathWidth / 2),
            new Vector3(-size, pathY, pathWidth / 2),
            new Vector3(0, 1, 0)
        );

        // Main vertical path
        pathBuilder.rect(
            new Vector3(-pathWidth / 2, pathY, -size),
            new Vector3(pathWidth / 2, pathY, -size),
            new Vector3(pathWidth / 2, pathY, size),
            new Vector3(-pathWidth / 2, pathY, size),
            new Vector3(0, 1, 0)
        );

        // Diagonal paths
        float diagWidth = 5f;
        pathBuilder.rect(
            new Vector3(-size + diagWidth, pathY, -size),
            new Vector3(-size, pathY, -size + diagWidth),
            new Vector3(size - diagWidth, pathY, size),
            new Vector3(size, pathY, size - diagWidth),
            new Vector3(0, 1, 0)
        );

        // Water pond - blue area
        com.badlogic.gdx.graphics.g3d.Material waterMaterial = new com.badlogic.gdx.graphics.g3d.Material(
            ColorAttribute.createDiffuse(new Color(0.3f, 0.5f, 0.8f, 1f)));
        com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder waterBuilder = modelBuilder.part("water",
            GL20.GL_TRIANGLES,
            com.badlogic.gdx.graphics.VertexAttributes.Usage.Position | com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal,
            waterMaterial);

        float pondX = 30f;
        float pondZ = 25f;
        float pondSize = 15f;
        waterBuilder.rect(
            new Vector3(pondX - pondSize, 0.03f, pondZ - pondSize),
            new Vector3(pondX + pondSize, 0.03f, pondZ - pondSize),
            new Vector3(pondX + pondSize, 0.03f, pondZ + pondSize),
            new Vector3(pondX - pondSize, 0.03f, pondZ + pondSize),
            new Vector3(0, 1, 0)
        );

        // Flower beds - colorful patches
        com.badlogic.gdx.graphics.g3d.Material flowerMaterial = new com.badlogic.gdx.graphics.g3d.Material(
            ColorAttribute.createDiffuse(new Color(0.9f, 0.4f, 0.5f, 1f)));
        com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder flowerBuilder = modelBuilder.part("flowers",
            GL20.GL_TRIANGLES,
            com.badlogic.gdx.graphics.VertexAttributes.Usage.Position | com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal,
            flowerMaterial);

        // Several flower patches
        addFlowerPatch(flowerBuilder, -35, -30, 8);
        addFlowerPatch(flowerBuilder, 40, -35, 10);
        addFlowerPatch(flowerBuilder, -25, 40, 7);

        return modelBuilder.end();
    }

    private void addFlowerPatch(com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder builder, float x, float z, float size) {
        builder.rect(
            new Vector3(x - size, 0.04f, z - size),
            new Vector3(x + size, 0.04f, z - size),
            new Vector3(x + size, 0.04f, z + size),
            new Vector3(x - size, 0.04f, z + size),
            new Vector3(0, 1, 0)
        );
    }

    private Model createTargetMarker() {
        modelBuilder.begin();

        com.badlogic.gdx.graphics.g3d.Material markerMaterial = new com.badlogic.gdx.graphics.g3d.Material(
            ColorAttribute.createDiffuse(new Color(1f, 0.3f, 0.3f, 0.7f)));
        com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder markerBuilder = modelBuilder.part("marker",
            GL20.GL_TRIANGLES,
            com.badlogic.gdx.graphics.VertexAttributes.Usage.Position | com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal,
            markerMaterial);

        // Simple cross/target on ground
        float size = 2f;
        float thickness = 0.3f;
        float y = 0.1f;

        // Horizontal bar
        markerBuilder.rect(
            new Vector3(-size, y, -thickness),
            new Vector3(size, y, -thickness),
            new Vector3(size, y, thickness),
            new Vector3(-size, y, thickness),
            new Vector3(0, 1, 0)
        );

        // Vertical bar
        markerBuilder.rect(
            new Vector3(-thickness, y, -size),
            new Vector3(thickness, y, -size),
            new Vector3(thickness, y, size),
            new Vector3(-thickness, y, size),
            new Vector3(0, 1, 0)
        );

        return modelBuilder.end();
    }

    private void spawnInitialTargets() {
        int targetCount = MathUtils.random(Constants.MIN_TARGETS, Constants.MAX_TARGETS);
        TargetType[] types = TargetType.values();

        for (int i = 0; i < targetCount; i++) {
            TargetType type = types[MathUtils.random(types.length - 1)];
            float x = MathUtils.random(-Constants.WORLD_WIDTH / 2 + 10, Constants.WORLD_WIDTH / 2 - 10);
            float z = MathUtils.random(-Constants.WORLD_HEIGHT / 2 + 10, Constants.WORLD_HEIGHT / 2 - 10);

            Target target = new Target(type, x, z);
            ModelInstance instance = new ModelInstance(targetModels.get(type.ordinal()));
            instance.transform.setToTranslation(x, 0, z);
            instance.transform.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
            target.setModelInstance(instance);
            physics.addTarget(target);
        }
    }

    @Override
    public void render(float delta) {
        // Handle pause/escape
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        // Update game state
        if (!gameState.isGameOver()) {
            update(delta);
        } else {
            // Transition to game over screen
            game.setScreen(new GameOverScreen(game, gameState, birdType));
            return;
        }

        // Clear screen - nice sky blue
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.5f, 0.7f, 0.95f, 1f);

        // Update camera
        updateCamera(delta);

        // Render 3D
        modelBatch.begin(camera);

        // Ground
        modelBatch.render(groundInstance, environment);

        // Target landing marker
        if (bird.canPoop()) {
            targetMarkerInstance.transform.setToTranslation(predictedLanding);
            modelBatch.render(targetMarkerInstance, environment);
        }

        // Targets
        for (int i = 0; i < physics.getTargets().size; i++) {
            Target target = physics.getTargets().get(i);
            if (target.getModelInstance() != null) {
                // Update target model position with scale
                target.getModelInstance().transform.setToTranslation(target.getPosition());
                target.getModelInstance().transform.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
                modelBatch.render(target.getModelInstance(), environment);
            }
        }

        // Power-ups
        for (int i = 0; i < physics.getPowerUps().size; i++) {
            PowerUp powerUp = physics.getPowerUps().get(i);
            if (powerUp.getModelInstance() != null) {
                modelBatch.render(powerUp.getModelInstance(), environment);
            }
        }

        // Poops
        for (int i = 0; i < physics.getActivePoops().size; i++) {
            Poop poop = physics.getActivePoops().get(i);
            if (poop.getModelInstance() != null) {
                poop.getModelInstance().transform.setToTranslation(poop.getPosition());
                poop.getModelInstance().transform.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
                modelBatch.render(poop.getModelInstance(), environment);
            }
        }

        // Bird
        modelBatch.render(birdInstance, environment);

        modelBatch.end();

        // Render HUD
        renderHUD();
    }

    private void update(float delta) {
        gameState.update(delta);

        // Process input
        inputProcessor.update();

        // Physics update
        physics.update(delta, bird, inputProcessor.getInputDirection());

        // Update bird model transform with scale
        updateBirdTransform();

        // Update predicted landing position
        predictedLanding = ProjectileCalculator.calculateLandingPosition(bird.getPosition(), bird.getVelocity());

        // Handle poop
        if (inputProcessor.isPoopRequested() && bird.canPoop()) {
            Poop poop = bird.poop();
            if (poop != null) {
                ModelInstance poopInstance = new ModelInstance(poopModel);
                poopInstance.transform.setToTranslation(poop.getPosition());
                poopInstance.transform.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
                poop.setModelInstance(poopInstance);
                physics.addPoop(poop);
            }
            inputProcessor.clearPoopRequest();
        }

        // Check for target hits and show feedback
        for (int i = 0; i < physics.getTargets().size; i++) {
            Target target = physics.getTargets().get(i);
            if (target.isHit() && target.getLastHitScore() > 0) {
                showHit(target.getLastHitZone(), target.getLastHitScore());
            }
        }

        // Update hit display
        if (hitDisplayTime > 0) {
            hitDisplayTime -= delta;
        }

        // Spawn targets periodically
        targetSpawnTimer += delta;
        if (targetSpawnTimer >= Constants.TARGET_SPAWN_INTERVAL) {
            targetSpawnTimer = 0;
            spawnRandomTarget();
        }

        // Spawn power-ups periodically
        powerUpSpawnTimer += delta;
        if (powerUpSpawnTimer >= Constants.POWERUP_SPAWN_INTERVAL) {
            powerUpSpawnTimer = 0;
            spawnPowerUp();
        }
    }

    private void showHit(String zone, int score) {
        hitMessage = zone;
        hitScore = score;
        hitDisplayTime = HIT_DISPLAY_DURATION;
    }

    private void updateCamera(float delta) {
        Vector3 birdPos = bird.getPosition();
        Vector3 birdVel = bird.getVelocity();

        // Calculate target camera angle based on bird velocity
        float targetAngle;
        if (birdVel.len2() > 1f) {
            targetAngle = MathUtils.atan2(birdVel.x, birdVel.z);
        } else {
            targetAngle = currentCameraAngle;
        }

        // Smooth camera rotation
        float angleDiff = targetAngle - currentCameraAngle;
        // Handle wrap-around
        while (angleDiff > MathUtils.PI) angleDiff -= MathUtils.PI2;
        while (angleDiff < -MathUtils.PI) angleDiff += MathUtils.PI2;
        currentCameraAngle += angleDiff * CAMERA_SMOOTH * delta;

        // Position camera behind bird
        float camX = birdPos.x - MathUtils.sin(currentCameraAngle) * CAMERA_DISTANCE;
        float camZ = birdPos.z - MathUtils.cos(currentCameraAngle) * CAMERA_DISTANCE;
        float camY = birdPos.y + CAMERA_HEIGHT;

        // Smooth camera position
        camera.position.lerp(new Vector3(camX, camY, camZ), CAMERA_SMOOTH * delta);

        // Look at bird (slightly ahead)
        float lookAheadDist = 5f;
        float lookX = birdPos.x + MathUtils.sin(currentCameraAngle) * lookAheadDist;
        float lookZ = birdPos.z + MathUtils.cos(currentCameraAngle) * lookAheadDist;
        camera.lookAt(lookX, birdPos.y - 2f, lookZ);

        camera.up.set(0, 1, 0);
        camera.update();
    }

    private void updateBirdTransform() {
        Vector3 pos = bird.getPosition();
        float rotation = bird.getRotationAngle();

        birdInstance.transform.setToTranslation(pos);
        birdInstance.transform.rotate(Vector3.Y, rotation);

        // Scale up the bird
        birdInstance.transform.scale(MODEL_SCALE * 1.5f, MODEL_SCALE * 1.5f, MODEL_SCALE * 1.5f);

        // Add slight tilt based on velocity for visual feedback
        Vector3 vel = bird.getVelocity();
        float tiltAngle = Math.min(20f, vel.len() * 0.3f);
        if (vel.len2() > 0.01f) {
            birdInstance.transform.rotate(Vector3.X, tiltAngle);
        }
    }

    private void renderHUD() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        spriteBatch.begin();

        // Score (top-left)
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "Score: " + gameState.getScore(), 20, screenHeight - 20);

        // Time remaining (top-center)
        int seconds = (int) gameState.getTimeRemaining();
        String timeText = String.format("Time: %d:%02d", seconds / 60, seconds % 60);
        layout.setText(font, timeText);
        font.draw(spriteBatch, timeText, (screenWidth - layout.width) / 2, screenHeight - 20);

        // Height (top-right)
        String heightText = String.format("Height: %.0fm", bird.getHeight());
        layout.setText(font, heightText);
        font.draw(spriteBatch, heightText, screenWidth - layout.width - 20, screenHeight - 20);

        // Wind indicator (top-right, below height)
        String windText = String.format("Wind: %.1f %s", physics.getWind().getSpeed(), physics.getWind().getDirectionName());
        layout.setText(font, windText);
        font.draw(spriteBatch, windText, screenWidth - layout.width - 20, screenHeight - 55);

        // Hit feedback (center of screen)
        if (hitDisplayTime > 0) {
            float alpha = Math.min(1f, hitDisplayTime / 0.5f);
            bigFont.setColor(1f, 1f, 0f, alpha);
            String feedbackText = hitMessage + " +" + hitScore;
            layout.setText(bigFont, feedbackText);
            bigFont.draw(spriteBatch, feedbackText,
                (screenWidth - layout.width) / 2,
                screenHeight / 2 + 100);
        }

        // Ready indicator
        if (bird.canPoop()) {
            font.setColor(Color.GREEN);
            font.draw(spriteBatch, "READY!", 20, 60);
        } else {
            font.setColor(Color.GRAY);
            font.draw(spriteBatch, "Reloading...", 20, 60);
        }

        spriteBatch.end();

        // Cooldown bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float barWidth = 200;
        float barHeight = 20;
        float barX = 20;
        float barY = 20;

        // Background
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Progress
        float progress = bird.getCooldownProgress();
        if (progress >= 1f) {
            shapeRenderer.setColor(0.2f, 0.9f, 0.2f, 1f);
        } else {
            shapeRenderer.setColor(0.9f, 0.6f, 0.2f, 1f);
        }
        shapeRenderer.rect(barX, barY, barWidth * progress, barHeight);

        shapeRenderer.end();

        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.end();
    }

    private void spawnRandomTarget() {
        if (physics.getTargets().size >= Constants.MAX_TARGETS) return;

        TargetType[] types = TargetType.values();
        TargetType type = types[MathUtils.random(types.length - 1)];
        float x = MathUtils.random(-Constants.WORLD_WIDTH / 2 + 10, Constants.WORLD_WIDTH / 2 - 10);
        float z = MathUtils.random(-Constants.WORLD_HEIGHT / 2 + 10, Constants.WORLD_HEIGHT / 2 - 10);

        Target target = new Target(type, x, z);
        ModelInstance instance = new ModelInstance(targetModels.get(type.ordinal()));
        instance.transform.setToTranslation(x, 0, z);
        instance.transform.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        target.setModelInstance(instance);
        physics.addTarget(target);
    }

    private void spawnPowerUp() {
        PowerUp.Type[] types = PowerUp.Type.values();
        PowerUp.Type type = types[MathUtils.random(types.length - 1)];

        float x = MathUtils.random(-Constants.WORLD_WIDTH / 2 + 15, Constants.WORLD_WIDTH / 2 - 15);
        float z = MathUtils.random(-Constants.WORLD_HEIGHT / 2 + 15, Constants.WORLD_HEIGHT / 2 - 15);

        PowerUp powerUp = new PowerUp(type, x, z);
        ModelInstance instance = new ModelInstance(powerUpModel);
        instance.transform.setToTranslation(x, 8f, z);
        instance.transform.scale(MODEL_SCALE * 2, MODEL_SCALE * 2, MODEL_SCALE * 2);
        powerUp.setModelInstance(instance);
        physics.addPowerUp(powerUp);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        inputProcessor.updateScreenCenter();
    }

    @Override
    public void pause() {
        gameState.setPaused(true);
    }

    @Override
    public void resume() {
        gameState.setPaused(false);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        bigFont.dispose();

        groundModel.dispose();
        birdModel.dispose();
        poopModel.dispose();
        for (int i = 0; i < targetModels.size; i++) {
            targetModels.get(i).dispose();
        }
        powerUpModel.dispose();
        targetMarkerModel.dispose();
    }
}
