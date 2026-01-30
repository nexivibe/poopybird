package ape.poopybird.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import ape.poopybird.util.Constants;

public class Wind {
    private final Vector3 velocity;
    private final Vector3 targetVelocity;
    private float changeTimer;
    private float transitionProgress;
    private static final float TRANSITION_DURATION = 2f;

    public Wind() {
        this.velocity = new Vector3();
        this.targetVelocity = new Vector3();
        this.changeTimer = 0;
        this.transitionProgress = 1f;
        generateNewTarget();
    }

    public void update(float delta) {
        changeTimer += delta;

        // Smoothly interpolate to target
        if (transitionProgress < 1f) {
            transitionProgress += delta / TRANSITION_DURATION;
            if (transitionProgress > 1f) transitionProgress = 1f;

            velocity.lerp(targetVelocity, transitionProgress);
        }

        // Generate new wind direction periodically
        if (changeTimer >= Constants.WIND_CHANGE_INTERVAL) {
            changeTimer = 0;
            generateNewTarget();
        }
    }

    private void generateNewTarget() {
        float angle = MathUtils.random(360f);
        float speed = MathUtils.random(Constants.MAX_WIND_SPEED);

        targetVelocity.x = MathUtils.cosDeg(angle) * speed;
        targetVelocity.y = 0;
        targetVelocity.z = MathUtils.sinDeg(angle) * speed;

        transitionProgress = 0f;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public float getSpeed() {
        return velocity.len();
    }

    public float getAngle() {
        return MathUtils.atan2(velocity.x, velocity.z) * MathUtils.radiansToDegrees;
    }

    public String getDirectionName() {
        float angle = getAngle();
        if (angle < 0) angle += 360;

        if (angle >= 337.5f || angle < 22.5f) return "N";
        if (angle >= 22.5f && angle < 67.5f) return "NE";
        if (angle >= 67.5f && angle < 112.5f) return "E";
        if (angle >= 112.5f && angle < 157.5f) return "SE";
        if (angle >= 157.5f && angle < 202.5f) return "S";
        if (angle >= 202.5f && angle < 247.5f) return "SW";
        if (angle >= 247.5f && angle < 292.5f) return "W";
        return "NW";
    }
}
