package com.joshh29012945.lemons;

/**
 * Superclass for all drawable things. It contains a bitmap for its texture
 * coordinates, tag and isDead bool
 */
public abstract class MasterClass {
    /**
     * position of the current object
     */
    float x, y;

    /**
     * Size of the current object
     */
    int w, h;

    /**
     * Defines what the class represents. Used to determine what object something is easily
     */
    Tag tag;

    /**
     * Determines if the object is dead. False by default.
     */
    boolean isDead;

    /**
     * Creates a platform object
     *
     * @param x - x coordinate of object
     * @param y - y coordinate of object
     * @param w - width of the object
     * @param h - height of the object
     */
    public MasterClass(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        isDead = false;
        OnCreate();
    }

    /**
     * Called once per frame. Does logic for each in-game element from which it devices
     */
    public abstract void Update();

    /**
     * Called on death. Used in very specific circumstances
     */
    public abstract void OnDeath();

    /**
     * Called from the constructor. Executes every time a new lemon is created
     */
    public abstract void OnCreate();

    /**
     * Called when clicked
     */
    public abstract void OnTouch();
}
