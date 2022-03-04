package com.joshh29012945.lemons;

import android.graphics.Paint;

/**
 * Superclass for all objects. It cannot be instantiated and contains the collide method prototype
 */
public abstract class Object extends MasterClass {
    /**
     * Used to determine the colour of the object
     */
    Paint paint;

    public Object(int x, int y, int w, int h) {
        super(x, y, w, h);
        paint = new Paint();
    }

    /**
     * Takes a "MasterClass" as a parameter. Is called when the object collides with another object
     *
     * @param masterClass - object this object collided with. Usually a lemon.
     */
    public abstract void OnCollide(MasterClass masterClass);

    protected abstract void OnButtonPressed();
}
