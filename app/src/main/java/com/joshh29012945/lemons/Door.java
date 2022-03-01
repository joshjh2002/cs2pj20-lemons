package com.joshh29012945.lemons;

import android.graphics.Color;

public class Door extends Object {

    public Door(int x, int y) {
        super(x, y, 32, 64);
        tag = Tag.DOOR;
        paint.setColor(Color.TRANSPARENT);
    }

    @Override
    public void OnCollide(MasterClass masterClass) {
        Lemon lemon = (Lemon) masterClass;
        lemon.isDead = true;
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
}
