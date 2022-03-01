package com.joshh29012945.lemons;

/**
 * Superclass for all lemons. Holds basic functions of each lemon
 */
public abstract class Lemon extends MasterClass {
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
     * Time the lemon will jump for in seconds
     */
    float timeJumping = 0;

    /**
     * Height of jump
     */
    float jumpHeight = 0;

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
            timeJumping += Game.frame_time;
            if (timeJumping >= jumpHeight) {
                jumping = 1;
            }
        } else {
            timeJumping = 0;
        }

        if (Game.touching) {
            this.x = Game.x;
            this.y = Game.y;
        }

        this.y += dy * jumping * Game.frame_time;
        this.x += dx * direction * Game.frame_time;
    }

    public void jump(float jumpHeightInSeconds) {
        jumping = -1;
        jumpHeight = jumpHeightInSeconds;
    }
}
