package com.joshh29012945.lemons;

import android.graphics.Color;

public class Exit extends Object {
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
    public void OnCollide(MasterClass masterClass) {

    }
}
