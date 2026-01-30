package ape.poopybird.util;

public class GameState {
    private int score;
    private float timeRemaining;
    private boolean gameOver;
    private boolean paused;
    private int poopCount;
    private int hitCount;

    public GameState() {
        reset();
    }

    public void reset() {
        score = 0;
        timeRemaining = Constants.GAME_DURATION;
        gameOver = false;
        paused = false;
        poopCount = 0;
        hitCount = 0;
    }

    public void update(float delta) {
        if (!paused && !gameOver) {
            timeRemaining -= delta;
            if (timeRemaining <= 0) {
                timeRemaining = 0;
                gameOver = true;
            }
        }
    }

    public void addScore(int points) {
        score += points;
    }

    public void incrementPoopCount() {
        poopCount++;
    }

    public void incrementHitCount() {
        hitCount++;
    }

    public int getScore() {
        return score;
    }

    public float getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getPoopCount() {
        return poopCount;
    }

    public int getHitCount() {
        return hitCount;
    }

    public float getAccuracy() {
        if (poopCount == 0) return 0f;
        return (float) hitCount / poopCount * 100f;
    }
}
