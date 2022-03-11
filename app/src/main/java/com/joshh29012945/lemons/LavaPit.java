package com.joshh29012945.lemons;

import android.graphics.Color;

/**
 * When collided with, the lemon will die
 */
public class LavaPit extends Object {

    public LavaPit(int x, int y, int w, int h) {
        super(x, y, w, h);
        tag = Tag.LAVA;
        paint.setColor(Color.rgb(255, 165, 0));
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
        Lemon lemon = (Lemon) masterClass;
        lemon.isDead = true;
    }

    @Override
    protected void OnButtonPressed() {

    }

    @Override
    protected void OnButtonPressedExit() {

    }
}
