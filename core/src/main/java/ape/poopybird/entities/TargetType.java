package ape.poopybird.entities;

public enum TargetType {
    // Larger hit zones for easier gameplay (kid/elderly friendly)
    PERSON("Person", 100, 50, 25, 1.5f, 3.0f, 5.0f, 1.8f),
    CAR("Car", 75, 40, 20, 2.5f, 5.0f, 8.0f, 1.2f),
    BENCH("Bench", 60, 30, 15, 2.0f, 4.0f, 6.0f, 0.8f),
    STATUE("Statue", 150, 75, 35, 1.8f, 3.5f, 5.5f, 3.0f),
    UMBRELLA("Umbrella", 80, 40, 20, 2.5f, 5.0f, 7.5f, 2.2f),
    PICNIC("Picnic", 50, 25, 10, 4.0f, 7.0f, 10.0f, 0.3f);

    private final String displayName;
    private final int centerScore;
    private final int middleScore;
    private final int outerScore;
    private final float centerRadius;
    private final float middleRadius;
    private final float outerRadius;
    private final float modelHeight;

    TargetType(String displayName, int centerScore, int middleScore, int outerScore,
               float centerRadius, float middleRadius, float outerRadius, float modelHeight) {
        this.displayName = displayName;
        this.centerScore = centerScore;
        this.middleScore = middleScore;
        this.outerScore = outerScore;
        this.centerRadius = centerRadius;
        this.middleRadius = middleRadius;
        this.outerRadius = outerRadius;
        this.modelHeight = modelHeight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getScoreForDistance(float distance) {
        if (distance <= centerRadius) return centerScore;
        if (distance <= middleRadius) return middleScore;
        if (distance <= outerRadius) return outerScore;
        return 0;
    }

    public String getZoneForDistance(float distance) {
        if (distance <= centerRadius) return "BULLSEYE!";
        if (distance <= middleRadius) return "GREAT!";
        if (distance <= outerRadius) return "HIT!";
        return "MISS";
    }

    public int getCenterScore() {
        return centerScore;
    }

    public int getMiddleScore() {
        return middleScore;
    }

    public int getOuterScore() {
        return outerScore;
    }

    public float getCenterRadius() {
        return centerRadius;
    }

    public float getMiddleRadius() {
        return middleRadius;
    }

    public float getOuterRadius() {
        return outerRadius;
    }

    public float getModelHeight() {
        return modelHeight;
    }
}
