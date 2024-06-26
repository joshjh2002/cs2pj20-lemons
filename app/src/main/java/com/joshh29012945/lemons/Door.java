package com.joshh29012945.lemons;

import android.graphics.Color;
import android.media.MediaPlayer;

/**
 * This class creates a door object. This object has a collide function which will kill a lemon,
 * essentially leaving the world through that door.
 */
public class Door extends Object {
    /**
     * Stores sound effect that the door plays when collided with
     */
    public static MediaPlayer exitEffect;
    /**
     * Takes an x and y position as inputs.
     *
     * @param x - coordinate
     * @param y - coordinate
     */
    public Door(int x, int y) {
        super(x, y, 32, 64);
        tag = Tag.DOOR;
        paint.setColor(Color.TRANSPARENT);
    }

    @Override
    public void OnCollide(MasterClass masterClass) {
        Lemon lemon = (Lemon) masterClass;
        if (!lemon.hasLeft)
            exitEffect.start();
        lemon.hasLeft = true;
    }

    @Override
    protected void OnButtonPressed() {

    }

    @Override
    protected void OnButtonPressedExit() {

    }

    @Override
    public void Update() {

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
}
