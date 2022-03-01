package com.joshh29012945.lemons;

import android.graphics.Bitmap;

/**
 * Standard lemon. Moves back and forth until dead
 */
public class StandardLemon extends Lemon {
    public static Bitmap image;

    public StandardLemon(int x, int y) {
        super(x, y, 64, 64);
        tag = Tag.STANDARD_LEMON;
    }

    @Override
    public void Update() {
        super.Update();
    }

    @Override
    public void OnDeath() {

    }

    @Override
    public void OnCreate() {

    }
}
