package ape.poopybird.entities;

public enum BirdType {
    SPARROW("Sparrow", 8f, 12f, 15f, 1.5f, 1, 0.8f, 0.5f, 0.3f),
    PIGEON("Pigeon", 10f, 16f, 18f, 1.2f, 2, 0.9f, 0.6f, 0.5f),
    CROW("Crow", 12f, 20f, 22f, 1.0f, 3, 1.0f, 0.7f, 0.2f),
    SEAGULL("Seagull", 15f, 25f, 28f, 0.9f, 4, 1.1f, 0.75f, 0.8f),
    HAWK("Hawk", 20f, 35f, 35f, 0.8f, 5, 1.3f, 0.9f, 0.4f),
    EAGLE("Eagle", 25f, 45f, 45f, 0.7f, 6, 1.5f, 1.0f, 0.35f);

    private final String displayName;
    private final float minHeight;
    private final float maxHeight;
    private final float baseVelocity;
    private final float cooldown;
    private final int difficulty;
    private final float scale;
    private final float wingSize;
    private final float colorHue;

    BirdType(String displayName, float minHeight, float maxHeight, float baseVelocity,
             float cooldown, int difficulty, float scale, float wingSize, float colorHue) {
        this.displayName = displayName;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.baseVelocity = baseVelocity;
        this.cooldown = cooldown;
        this.difficulty = difficulty;
        this.scale = scale;
        this.wingSize = wingSize;
        this.colorHue = colorHue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getMinHeight() {
        return minHeight;
    }

    public float getMaxHeight() {
        return maxHeight;
    }

    public float getBaseVelocity() {
        return baseVelocity;
    }

    public float getCooldown() {
        return cooldown;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public float getScale() {
        return scale;
    }

    public float getWingSize() {
        return wingSize;
    }

    public float getColorHue() {
        return colorHue;
    }

    public String getDifficultyLabel() {
        switch (difficulty) {
            case 1: return "Easy";
            case 2: return "Easy+";
            case 3: return "Medium";
            case 4: return "Medium+";
            case 5: return "Hard";
            case 6: return "Expert";
            default: return "Unknown";
        }
    }
}
