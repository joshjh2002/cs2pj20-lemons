package com.joshh29012945.lemons;

import android.graphics.Color;

/**
 * An invisible object of fixed size that will move lemons from the buffer to the active list
 */
public class Spawner extends Object {
    /**
     * Holds current game object so that it can directly interact with its methods and lists
     */
    Game currentGame;
    /**
     * Time since its creation/last spawn
     */
    float elapsed_time;
    /**
     * Time, in seconds, after which a new lemon will spawn
     */
    float spawn_delay;

    public Spawner(int x, int y, Game currentGame, float spawn_delay) {
        super(x, y, 64, 64);
        this.currentGame = currentGame;
        this.tag = Tag.SPAWNER;
        this.paint.setColor(Color.TRANSPARENT);
        this.spawn_delay = spawn_delay;
        this.elapsed_time = spawn_delay;
    }

    @Override
    public void Update() {
        elapsed_time += Game.frame_time;

        if (currentGame.lemonsBuffer.size() > 0 && elapsed_time >= spawn_delay) {
            Lemon lemon = currentGame.lemonsBuffer.get(0);
            lemon.x = this.x;
            lemon.y = this.y;
            currentGame.lemons.add(lemon);
            currentGame.lemonsBuffer.remove(0);
            elapsed_time = 0;
        }
    }

    @Override
    public void OnDeath() {

    }

    @Override
    public void OnCreate() {

    }

    @Override
    public void OnTouch() {

    }

    @Override
    public void OnCollide(MasterClass masterClass) {

    }

    @Override
    protected void OnButtonPressed() {

    }

    @Override
    protected void OnButtonPressedExit() {

    }
}
