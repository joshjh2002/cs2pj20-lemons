package com.joshh29012945.lemons;

import android.graphics.Color;

/**
 * This class creates a door object. This object has a collide function which will kill a lemon,
 * essentially leaving the world through that door.
 */
public class Door extends Object {
    /**
     * Takes an x and y position as inputs.
     *
     * @param x
     * @param y
     */
    public Door(int x, int y) {
        super(x, y, 32, 64);
        tag = Tag.DOOR;
        paint.setColor(Color.TRANSPARENT);
    }

    @Override
    public void OnCollide(MasterClass masterClass) {
        Lemon lemon = (Lemon) masterClass;
        lemon.isDead = true;
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
}
