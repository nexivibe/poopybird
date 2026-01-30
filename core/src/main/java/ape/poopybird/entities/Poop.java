package ape.poopybird.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import ape.poopybird.util.Constants;

public class Poop {
    private final Vector3 position;
    private final Vector3 velocity;
    private final Vector3 startPosition;
    private ModelInstance modelInstance;
    private boolean landed;
    private float landingTime;
    private static final float SPLAT_DURATION = 0.5f;

    public Poop(Vector3 startPosition, Vector3 inheritedVelocity) {
        this.startPosition = new Vector3(startPosition);
        this.position = new Vector3(startPosition);
        this.velocity = new Vector3(inheritedVelocity.x, 0, inheritedVelocity.z);
        this.landed = false;
        this.landingTime = 0;
    }

    public void update(float delta) {
        if (landed) {
            landingTime += delta;
            return;
        }

        // Apply gravity
        velocity.y -= Constants.GRAVITY * delta;

        // Update position
        position.add(velocity.x * delta, velocity.y * delta, velocity.z * delta);

        // Check if landed
        if (position.y <= Constants.GROUND_LEVEL) {
            position.y = Constants.GROUND_LEVEL;
            landed = true;
        }

        // Update model instance
        if (modelInstance != null) {
            modelInstance.transform.setToTranslation(position);
            if (landed) {
                // Scale down for splat effect
                float scale = Math.max(0.1f, 1f - landingTime / SPLAT_DURATION);
                modelInstance.transform.scale(1f, scale, 1f);
            }
        }
    }

    public boolean shouldRemove() {
        return landed && landingTime > SPLAT_DURATION;
    }

    public boolean hasJustLanded() {
        return landed && landingTime == 0;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public Vector3 getStartPosition() {
        return startPosition;
    }

    public boolean isLanded() {
        return landed;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setModelInstance(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }

    public float getLandingX() {
        return position.x;
    }

    public float getLandingZ() {
        return position.z;
    }
}
