package com.joshh29012945.lemons;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * When collided with, a lemon will jump
 */
public class JumpPad extends Object {
    public static Bitmap image;

    float jump_height;

    public JumpPad(int x, int y, float h) {
        super(x, y, 96, 55);
        paint.setColor(Color.BLUE);
        tag = Tag.JUMP;
        jump_height = h;
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

        lemon.jump(jump_height);
    }

    @Override
    protected void OnButtonPressed() {

    }

    @Override
    protected void OnButtonPressedExit() {

    }
}
