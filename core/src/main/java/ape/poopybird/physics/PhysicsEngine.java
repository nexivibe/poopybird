package ape.poopybird.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import ape.poopybird.entities.Bird;
import ape.poopybird.entities.Poop;
import ape.poopybird.entities.PowerUp;
import ape.poopybird.entities.Target;
import ape.poopybird.util.Constants;
import ape.poopybird.util.GameState;

public class PhysicsEngine {
    private final Wind wind;
    private final Array<Poop> activePoops;
    private final Array<Target> targets;
    private final Array<PowerUp> powerUps;
    private final GameState gameState;

    private static final float POWERUP_COLLECT_RADIUS = 3f;

    public PhysicsEngine(GameState gameState) {
        this.wind = new Wind();
        this.activePoops = new Array<>();
        this.targets = new Array<>();
        this.powerUps = new Array<>();
        this.gameState = gameState;
    }

    public void update(float delta, Bird bird, Vector3 inputDirection) {
        // Update wind
        wind.update(delta);

        // Update bird
        bird.update(delta, inputDirection, wind.getVelocity());

        // Update poops
        updatePoops(delta);

        // Update targets
        updateTargets(delta);

        // Update power-ups and check collection
        updatePowerUps(delta, bird);
    }

    private void updatePoops(float delta) {
        for (int i = activePoops.size - 1; i >= 0; i--) {
            Poop poop = activePoops.get(i);
            boolean wasInAir = !poop.isLanded();
            poop.update(delta);

            // Check for target hits when poop lands
            if (wasInAir && poop.isLanded()) {
                checkPoopHits(poop);
            }

            // Remove old poops
            if (poop.shouldRemove()) {
                activePoops.removeIndex(i);
            }
        }
    }

    private void checkPoopHits(Poop poop) {
        float poopX = poop.getLandingX();
        float poopZ = poop.getLandingZ();

        int totalScore = 0;
        for (Target target : targets) {
            int score = target.checkHit(poopX, poopZ);
            if (score > 0) {
                totalScore += score;
                gameState.incrementHitCount();
            }
        }

        if (totalScore > 0) {
            gameState.addScore(totalScore);
        }
    }

    private void updateTargets(float delta) {
        for (Target target : targets) {
            target.update(delta, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        }
    }

    private void updatePowerUps(float delta, Bird bird) {
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            powerUp.update(delta);

            if (powerUp.checkCollection(bird.getPosition(), POWERUP_COLLECT_RADIUS)) {
                applyPowerUp(powerUp, bird);
            }

            if (powerUp.shouldRemove()) {
                powerUps.removeIndex(i);
            }
        }
    }

    private void applyPowerUp(PowerUp powerUp, Bird bird) {
        switch (powerUp.getType()) {
            case COOLDOWN_REDUCTION:
                bird.applyCooldownReduction(powerUp.getType().getCooldownReduction());
                break;
            case DOUBLE_POINTS:
                // Could implement score multiplier in GameState
                break;
            case MULTI_POOP:
                // Could implement multi-poop ability
                break;
        }
    }

    public void addPoop(Poop poop) {
        if (poop != null) {
            activePoops.add(poop);
            gameState.incrementPoopCount();
        }
    }

    public void addTarget(Target target) {
        targets.add(target);
    }

    public void addPowerUp(PowerUp powerUp) {
        powerUps.add(powerUp);
    }

    public void clearAll() {
        activePoops.clear();
        targets.clear();
        powerUps.clear();
    }

    public Wind getWind() {
        return wind;
    }

    public Array<Poop> getActivePoops() {
        return activePoops;
    }

    public Array<Target> getTargets() {
        return targets;
    }

    public Array<PowerUp> getPowerUps() {
        return powerUps;
    }

    public Vector3 getPredictedLanding(Bird bird) {
        return ProjectileCalculator.calculateLandingPosition(bird.getPosition(), bird.getVelocity());
    }
}
