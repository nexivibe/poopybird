package ape.poopybird.physics;

import com.badlogic.gdx.math.Vector3;
import ape.poopybird.util.Constants;

public class ProjectileCalculator {

    /**
     * Calculate where a poop will land given the bird's current state.
     */
    public static Vector3 calculateLandingPosition(Vector3 dropPosition, Vector3 velocity) {
        float height = dropPosition.y - Constants.GROUND_LEVEL;
        if (height <= 0) {
            return new Vector3(dropPosition.x, Constants.GROUND_LEVEL, dropPosition.z);
        }

        // Time to fall: t = sqrt(2 * h / g)
        float fallTime = (float) Math.sqrt(2 * height / Constants.GRAVITY);

        // Landing position considering horizontal velocity
        float landingX = dropPosition.x + velocity.x * fallTime;
        float landingZ = dropPosition.z + velocity.z * fallTime;

        return new Vector3(landingX, Constants.GROUND_LEVEL, landingZ);
    }

    /**
     * Calculate the time it takes for poop to fall from current height.
     */
    public static float calculateFallTime(float height) {
        if (height <= 0) return 0;
        return (float) Math.sqrt(2 * height / Constants.GRAVITY);
    }

    /**
     * Get predicted trajectory points for visualization.
     */
    public static Vector3[] calculateTrajectoryPoints(Vector3 dropPosition, Vector3 velocity, int numPoints) {
        Vector3[] points = new Vector3[numPoints];
        float height = dropPosition.y - Constants.GROUND_LEVEL;
        float totalTime = calculateFallTime(height);

        for (int i = 0; i < numPoints; i++) {
            float t = (totalTime / (numPoints - 1)) * i;
            float x = dropPosition.x + velocity.x * t;
            float y = dropPosition.y - 0.5f * Constants.GRAVITY * t * t;
            float z = dropPosition.z + velocity.z * t;

            points[i] = new Vector3(x, Math.max(Constants.GROUND_LEVEL, y), z);
        }

        return points;
    }

    /**
     * Calculate the horizontal distance the poop will travel.
     */
    public static float calculateHorizontalDistance(float height, Vector3 velocity) {
        float fallTime = calculateFallTime(height);
        float horizontalSpeed = (float) Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        return horizontalSpeed * fallTime;
    }
}
