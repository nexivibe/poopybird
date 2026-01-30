package ape.poopybird.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Bird {
    private final BirdType type;
    private final Vector3 position;
    private final Vector3 velocity;
    private ModelInstance modelInstance;
    private float currentCooldown;
    private float cooldownMultiplier;
    private float time;
    private float wingAngle;
    private float flapSpeed;

    public Bird(BirdType type) {
        this.type = type;
        this.position = new Vector3(0, type.getMinHeight(), 0);
        this.velocity = new Vector3();
        this.currentCooldown = 0;
        this.cooldownMultiplier = 1f;
        this.time = 0;
        this.wingAngle = 0;
        this.flapSpeed = 8f;
    }

    public void update(float delta, Vector3 inputDirection, Vector3 windVelocity) {
        time += delta;

        // Update cooldown
        if (currentCooldown > 0) {
            currentCooldown -= delta;
            if (currentCooldown < 0) currentCooldown = 0;
        }

        // Calculate effective velocity
        velocity.set(inputDirection).scl(type.getBaseVelocity()).add(windVelocity);

        // Update position
        position.x += velocity.x * delta;
        position.z += velocity.z * delta;

        // Height oscillation
        float heightRange = type.getMaxHeight() - type.getMinHeight();
        position.y = type.getMinHeight() + heightRange / 2f * (1f + MathUtils.sin(time * 0.5f));

        // Wing flapping animation
        wingAngle = MathUtils.sin(time * flapSpeed) * 30f;

        // Update model instance position if set
        if (modelInstance != null) {
            modelInstance.transform.setToTranslation(position);
            modelInstance.transform.rotate(Vector3.Y, getRotationAngle());
        }
    }

    public boolean canPoop() {
        return currentCooldown <= 0;
    }

    public Poop poop() {
        if (!canPoop()) return null;

        currentCooldown = type.getCooldown() * cooldownMultiplier;
        return new Poop(new Vector3(position), new Vector3(velocity));
    }

    public float getRotationAngle() {
        if (velocity.len2() < 0.01f) return 0;
        return MathUtils.atan2(velocity.x, velocity.z) * MathUtils.radiansToDegrees;
    }

    public void applyCooldownReduction(float reduction) {
        cooldownMultiplier = Math.max(0.3f, cooldownMultiplier - reduction);
    }

    public void resetCooldownMultiplier() {
        cooldownMultiplier = 1f;
    }

    public BirdType getType() {
        return type;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public float getCooldownProgress() {
        if (type.getCooldown() <= 0) return 1f;
        return 1f - (currentCooldown / (type.getCooldown() * cooldownMultiplier));
    }

    public float getWingAngle() {
        return wingAngle;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setModelInstance(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }

    public float getHeight() {
        return position.y;
    }
}
