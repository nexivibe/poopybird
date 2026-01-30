package ape.poopybird.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class PowerUp {
    public enum Type {
        COOLDOWN_REDUCTION("Speed Boost", 0.3f),
        DOUBLE_POINTS("2x Points", 0f),
        MULTI_POOP("Triple Shot", 0f);

        private final String displayName;
        private final float cooldownReduction;

        Type(String displayName, float cooldownReduction) {
            this.displayName = displayName;
            this.cooldownReduction = cooldownReduction;
        }

        public String getDisplayName() {
            return displayName;
        }

        public float getCooldownReduction() {
            return cooldownReduction;
        }
    }

    private final Type type;
    private final Vector3 position;
    private ModelInstance modelInstance;
    private float bobTime;
    private float rotationAngle;
    private boolean collected;
    private float lifetime;
    private static final float MAX_LIFETIME = 10f;
    private static final float BOB_SPEED = 3f;
    private static final float BOB_HEIGHT = 0.5f;
    private static final float ROTATION_SPEED = 90f;
    private static final float COLLECT_HEIGHT = 3f;

    public PowerUp(Type type, float x, float z) {
        this.type = type;
        this.position = new Vector3(x, COLLECT_HEIGHT, z);
        this.bobTime = MathUtils.random(MathUtils.PI2);
        this.rotationAngle = MathUtils.random(360f);
        this.collected = false;
        this.lifetime = 0;
    }

    public void update(float delta) {
        if (collected) return;

        lifetime += delta;
        bobTime += delta * BOB_SPEED;
        rotationAngle += delta * ROTATION_SPEED;

        // Bob up and down
        float bobOffset = MathUtils.sin(bobTime) * BOB_HEIGHT;

        if (modelInstance != null) {
            modelInstance.transform.setToTranslation(position.x, position.y + bobOffset, position.z);
            modelInstance.transform.rotate(Vector3.Y, rotationAngle);
        }
    }

    public boolean checkCollection(Vector3 birdPosition, float collectRadius) {
        if (collected) return false;

        float dx = birdPosition.x - position.x;
        float dy = birdPosition.y - position.y;
        float dz = birdPosition.z - position.z;
        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance < collectRadius) {
            collected = true;
            return true;
        }
        return false;
    }

    public boolean shouldRemove() {
        return collected || lifetime > MAX_LIFETIME;
    }

    public boolean isExpiring() {
        return lifetime > MAX_LIFETIME - 3f;
    }

    public Type getType() {
        return type;
    }

    public Vector3 getPosition() {
        return position;
    }

    public boolean isCollected() {
        return collected;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setModelInstance(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }
}
