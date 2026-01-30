package ape.poopybird;

import com.badlogic.gdx.Game;
import ape.poopybird.screens.SplashScreen;

/**
 * Main game class for PoopyBird.
 * Extends Game for screen management across Splash, Menu, Bird Selection, Game, and Game Over screens.
 */
public class Main extends Game {

    @Override
    public void create() {
        setScreen(new SplashScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
