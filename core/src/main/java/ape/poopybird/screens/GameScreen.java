package ape.poopybird.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import ape.poopybird.Main;
import ape.poopybird.entities.*;
import ape.poopybird.graphics.*;
import ape.poopybird.input.GameInputProcessor;
import ape.poopybird.physics.PhysicsEngine;
import ape.poopybird.ui.HUD;
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

    // Instances
    private ModelInstance groundInstance;
    private ModelInstance birdInstance;

    // Game objects
    private Bird bird;
    private PhysicsEngine physics;
    private GameState gameState;
    private GameInputProcessor inputProcessor;
    private HUD hud;
    private SpriteBatch spriteBatch;

    // Target spawning
    private float targetSpawnTimer;
    private float powerUpSpawnTimer;

    public GameScreen(Main game, BirdType birdType) {
        this.game = game;
        this.birdType = birdType;
        this.targetModels = new Array<>();
    }

    @Override
    public void show() {
        // Initialize rendering
        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        spriteBatch = new SpriteBatch();

        // Setup camera (top-down perspective)
        camera = new PerspectiveCamera(Constants.CAMERA_FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, Constants.CAMERA_HEIGHT, 0);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 200f;
        camera.update();

        // Setup lighting
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, -0.3f));

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

        // Setup input
        inputProcessor = new GameInputProcessor();
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(inputProcessor);
        Gdx.input.setInputProcessor(multiplexer);

        // Setup HUD
        hud = new HUD();

        // Spawn initial targets
        spawnInitialTargets();

        // Reset timers
        targetSpawnTimer = 0;
        powerUpSpawnTimer = 0;
    }

    private void createModels() {
        // Ground
        groundModel = GroundPlaneBuilder.buildGround(modelBuilder, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Bird
        birdModel = OrigamiBirdBuilder.buildBird(modelBuilder, birdType);

        // Poop
        poopModel = PoopModelBuilder.buildPoop(modelBuilder);

        // Targets
        for (TargetType type : TargetType.values()) {
            targetModels.add(TargetModelBuilder.buildTarget(modelBuilder, type));
        }

        // Power-up
        powerUpModel = PoopModelBuilder.buildPowerUp(modelBuilder);
    }

    private void spawnInitialTargets() {
        int targetCount = MathUtils.random(Constants.MIN_TARGETS, Constants.MAX_TARGETS);
        TargetType[] types = TargetType.values();

        for (int i = 0; i < targetCount; i++) {
            TargetType type = types[MathUtils.random(types.length - 1)];
            float x = MathUtils.random(-Constants.WORLD_WIDTH / 2 + 5, Constants.WORLD_WIDTH / 2 - 5);
            float z = MathUtils.random(-Constants.WORLD_HEIGHT / 2 + 5, Constants.WORLD_HEIGHT / 2 - 5);

            Target target = new Target(type, x, z);
            ModelInstance instance = new ModelInstance(targetModels.get(type.ordinal()));
            instance.transform.setToTranslation(x, 0, z);
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

        // Clear screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.4f, 0.6f, 0.9f, 1f); // Sky blue

        // Update camera to follow bird
        updateCamera();

        // Render 3D
        modelBatch.begin(camera);

        // Ground
        modelBatch.render(groundInstance, environment);

        // Targets
        for (Target target : physics.getTargets()) {
            if (target.getModelInstance() != null) {
                modelBatch.render(target.getModelInstance(), environment);
            }
        }

        // Power-ups
        for (PowerUp powerUp : physics.getPowerUps()) {
            if (powerUp.getModelInstance() != null) {
                modelBatch.render(powerUp.getModelInstance(), environment);
            }
        }

        // Poops
        for (Poop poop : physics.getActivePoops()) {
            if (poop.getModelInstance() != null) {
                modelBatch.render(poop.getModelInstance(), environment);
            }
        }

        // Bird
        modelBatch.render(birdInstance, environment);

        modelBatch.end();

        // Render HUD
        hud.update(delta);
        hud.render(spriteBatch, gameState, bird, physics.getWind());
    }

    private void update(float delta) {
        gameState.update(delta);

        // Process input
        inputProcessor.update();

        // Physics update
        physics.update(delta, bird, inputProcessor.getInputDirection());

        // Update bird model transform
        updateBirdTransform();

        // Handle poop
        if (inputProcessor.isPoopRequested() && bird.canPoop()) {
            Poop poop = bird.poop();
            if (poop != null) {
                ModelInstance poopInstance = new ModelInstance(poopModel);
                poopInstance.transform.setToTranslation(poop.getPosition());
                poop.setModelInstance(poopInstance);
                physics.addPoop(poop);
            }
            inputProcessor.clearPoopRequest();
        }

        // Check for target hits and show feedback
        for (Target target : physics.getTargets()) {
            if (target.isHit() && target.getLastHitScore() > 0) {
                hud.showHit(target.getLastHitZone(), target.getLastHitScore());
            }
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

    private void updateCamera() {
        // Smooth camera follow
        Vector3 birdPos = bird.getPosition();
        camera.position.x = birdPos.x;
        camera.position.z = birdPos.z;
        camera.position.y = Constants.CAMERA_HEIGHT;

        // Look at bird position
        camera.lookAt(birdPos.x, 0, birdPos.z);
        camera.up.set(0, 0, -1); // Keep north up
        camera.update();
    }

    private void updateBirdTransform() {
        Vector3 pos = bird.getPosition();
        float rotation = bird.getRotationAngle();

        birdInstance.transform.setToTranslation(pos);
        birdInstance.transform.rotate(Vector3.Y, rotation);

        // Add slight tilt based on velocity for visual feedback
        Vector3 vel = bird.getVelocity();
        float tiltAngle = Math.min(15f, vel.len() * 0.5f);
        if (vel.len2() > 0.01f) {
            birdInstance.transform.rotate(Vector3.X, tiltAngle);
        }
    }

    private void spawnRandomTarget() {
        if (physics.getTargets().size >= Constants.MAX_TARGETS) return;

        TargetType[] types = TargetType.values();
        TargetType type = types[MathUtils.random(types.length - 1)];
        float x = MathUtils.random(-Constants.WORLD_WIDTH / 2 + 5, Constants.WORLD_WIDTH / 2 - 5);
        float z = MathUtils.random(-Constants.WORLD_HEIGHT / 2 + 5, Constants.WORLD_HEIGHT / 2 - 5);

        Target target = new Target(type, x, z);
        ModelInstance instance = new ModelInstance(targetModels.get(type.ordinal()));
        instance.transform.setToTranslation(x, 0, z);
        target.setModelInstance(instance);
        physics.addTarget(target);
    }

    private void spawnPowerUp() {
        PowerUp.Type[] types = PowerUp.Type.values();
        PowerUp.Type type = types[MathUtils.random(types.length - 1)];

        float x = MathUtils.random(-Constants.WORLD_WIDTH / 2 + 10, Constants.WORLD_WIDTH / 2 - 10);
        float z = MathUtils.random(-Constants.WORLD_HEIGHT / 2 + 10, Constants.WORLD_HEIGHT / 2 - 10);

        PowerUp powerUp = new PowerUp(type, x, z);
        ModelInstance instance = new ModelInstance(powerUpModel);
        instance.transform.setToTranslation(x, 3f, z);
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

        groundModel.dispose();
        birdModel.dispose();
        poopModel.dispose();
        for (Model model : targetModels) {
            model.dispose();
        }
        powerUpModel.dispose();

        hud.dispose();
    }
}
