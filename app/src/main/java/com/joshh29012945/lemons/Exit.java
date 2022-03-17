package com.joshh29012945.lemons;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Exit object does nothing. It shows a door object.
 */
public class Exit extends Object {
    public static Bitmap image;

    public Exit(int x, int y) {
        super(x, y, 96, 128);
        tag = Tag.EXIT;
        paint.setColor(Color.GREEN);
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
