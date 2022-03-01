package com.joshh29012945.lemons;

import android.graphics.Color;

public class JumpPad extends Object{
    public JumpPad(int x, int y, int w, int h) {
        super(x, y, w, h);
        paint.setColor(Color.BLUE);
        tag = Tag.JUMP;
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
        Lemon lemon = (Lemon)masterClass;

        lemon.jump(0.5f);
    }
}
