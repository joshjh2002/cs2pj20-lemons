package com.joshh29012945.lemons;

import android.graphics.Color;

/**
 * A platform is a collidable object that the lemons can walk on
 */
public class Platform extends Object {
    /**
     * Creates a platform object
     *
     * @param x - x coordinate of object
     * @param y - y coordinate of object
     * @param w - width of the platform
     * @param h - height of the platform
     */
    public Platform(int x, int y, int w, int h) {
        super(x, y, w, h);
        paint.setColor(Color.BLACK);
        tag = Tag.PLATFORM;
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
    public void OnCollide(MasterClass masterClass) {
        Lemon lemon = (Lemon) masterClass;
        lemon.isColliding = true;
        lemon.dy = 0;

        //If lemon is on top of the platform
        if (lemon.y > this.y) {
            if (lemon.x < this.x) // if lemon is on the left of the object
                lemon.direction = -1;
            else  // if lemon is on the right of the object
                lemon.direction = 1;
        }
    }

    @Override
    protected void OnButtonPressed() {

    }
}
