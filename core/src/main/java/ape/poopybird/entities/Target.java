package ape.poopybird.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Target {
    private final TargetType type;
    private final Vector3 position;
    private final Vector3 velocity;
    private ModelInstance modelInstance;
    private boolean hit;
    private float hitTime;
    private int lastHitScore;
    private String lastHitZone;
    private static final float HIT_DISPLAY_DURATION = 1.5f;

    public Target(TargetType type, float x, float z) {
        this.type = type;
        this.position = new Vector3(x, 0, z);
        this.velocity = new Vector3();
        this.hit = false;
        this.hitTime = 0;
        this.lastHitScore = 0;
        this.lastHitZone = "";

        // Give some targets random movement
        if (type == TargetType.PERSON) {
            float angle = MathUtils.random(360f);
            float speed = MathUtils.random(1f, 3f);
            velocity.x = MathUtils.cosDeg(angle) * speed;
            velocity.z = MathUtils.sinDeg(angle) * speed;
        }
    }

    public void update(float delta, float worldWidth, float worldHeight) {
        // Update hit display timer
        if (hit) {
            hitTime += delta;
            if (hitTime > HIT_DISPLAY_DURATION) {
                hit = false;
                hitTime = 0;
            }
        }

        // Move targets that have velocity
        if (velocity.len2() > 0.01f) {
            position.x += velocity.x * delta;
            position.z += velocity.z * delta;

            // Bounce off world boundaries
            float margin = 5f;
            if (position.x < -worldWidth / 2 + margin || position.x > worldWidth / 2 - margin) {
                velocity.x = -velocity.x;
                position.x = MathUtils.clamp(position.x, -worldWidth / 2 + margin, worldWidth / 2 - margin);
            }
            if (position.z < -worldHeight / 2 + margin || position.z > worldHeight / 2 - margin) {
                velocity.z = -velocity.z;
                position.z = MathUtils.clamp(position.z, -worldHeight / 2 + margin, worldHeight / 2 - margin);
            }
        }

        // Update model instance
        if (modelInstance != null) {
            modelInstance.transform.setToTranslation(position);
            if (velocity.len2() > 0.01f) {
                float angle = MathUtils.atan2(velocity.x, velocity.z) * MathUtils.radiansToDegrees;
                modelInstance.transform.rotate(Vector3.Y, angle);
            }
        }
    }

    public int checkHit(float poopX, float poopZ) {
        float dx = poopX - position.x;
        float dz = poopZ - position.z;
        float distance = (float) Math.sqrt(dx * dx + dz * dz);

        int score = type.getScoreForDistance(distance);
        if (score > 0) {
            hit = true;
            hitTime = 0;
            lastHitScore = score;
            lastHitZone = type.getZoneForDistance(distance);
        }
        return score;
    }

    public TargetType getType() {
        return type;
    }

    public Vector3 getPosition() {
        return position;
    }

    public boolean isHit() {
        return hit;
    }

    public int getLastHitScore() {
        return lastHitScore;
    }

    public String getLastHitZone() {
        return lastHitZone;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setModelInstance(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }
}
