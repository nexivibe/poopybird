package ape.poopybird.util;

public final class Constants {
    private Constants() {}

    // Game timing
    public static final float GAME_DURATION = 90f;
    public static final float SPLASH_DURATION = 1f;

    // World dimensions
    public static final float WORLD_WIDTH = 100f;
    public static final float WORLD_HEIGHT = 100f;
    public static final float GROUND_LEVEL = 0f;

    // Camera settings
    public static final float CAMERA_HEIGHT = 60f;
    public static final float CAMERA_FOV = 67f;

    // Physics
    public static final float GRAVITY = 9.8f;

    // Wind
    public static final float MAX_WIND_SPEED = 5f;
    public static final float WIND_CHANGE_INTERVAL = 10f;

    // Power-ups
    public static final float POWERUP_SPAWN_INTERVAL = 15f;
    public static final float POWERUP_DURATION = 5f;
    public static final float COOLDOWN_REDUCTION = 0.5f;

    // Target spawn
    public static final int MIN_TARGETS = 8;
    public static final int MAX_TARGETS = 15;
    public static final float TARGET_SPAWN_INTERVAL = 5f;

    // UI
    public static final int VIRTUAL_WIDTH = 640;
    public static final int VIRTUAL_HEIGHT = 480;
}
