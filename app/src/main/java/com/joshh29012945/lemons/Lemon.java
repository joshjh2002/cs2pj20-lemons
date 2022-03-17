package com.joshh29012945.lemons;

import android.util.Log;

/**
 * Superclass for all lemons. Holds basic functions of each lemon
 */
public abstract class Lemon extends MasterClass {
    public boolean isLeft;
    /**
     * Determines movement direction
     */
    double dx, dy = 300;

    /**
     * If currently colliding with something. Used in logic to determine dy
     */
    Boolean isColliding = false;

    /**
     * Movement direction and speed. If positive, then move right. Increase scale to determine speed.
     */
    int direction = 1;

    /**
     * Determines if the lemon is jumping. Also used to determine the effect of gravity
     */
    int jumping = 1;

    /**
     * Height of jump
     */
    float jumpHeight = 0;

    boolean canMove = true;

    public Lemon(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public void Update() {
        if (!isColliding) {
            dy = 300;
            dx = 150;
        } else {
            dx = 200;
        }

        if (jumping == -1) {
            dy = 300;
            if (this.y <= jumpHeight) {
                jumping = 1;
            }
        }

        if (canMove)
            this.x += dx * direction * Game.FrameTime();

        this.y += dy * jumping * Game.FrameTime();

    }

    public void jump(float jumpToY) {
        jumping = -1;
        jumpHeight =  jumpToY;
    }

}
