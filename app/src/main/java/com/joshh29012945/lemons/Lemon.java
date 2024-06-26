package com.joshh29012945.lemons;

import android.util.Log;

/**
 * Superclass for all lemons. Holds basic functions of each lemon
 */
public abstract class Lemon extends MasterClass {
    public boolean hasLeft;
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

    /**
     * decides whether the lemon can move. Is used in a few circumstances
     */
    boolean canMove = true;

    public Lemon(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public void Update() {
        // if lemon is not colliding with anything, it should move 300px down and 150px across each second
        if (!isColliding) {
            dy = 300;
            dx = 150;
        } else { //lemon only moves left and right when not falling
            dx = 200;
        }

        //if the lemon is jumping (-1 for direction of gravity)
        if (jumping == -1) {
            dy = 300;
            //continue to move up until it has reached jump height
            if (this.y <= jumpHeight) {
                jumping = 1;
            }
        }

        if (canMove)
            this.x += dx * direction * Game.FrameTime();

        //lemon will always be effected by gravity
        this.y += dy * jumping * Game.FrameTime();

    }

    public void jump(float jumpToY) {
        jumping = -1;
        jumpHeight = jumpToY;
    }

}
