package com.joshh29012945.lemons;

import android.graphics.Color;

public class Platform extends Object {
    public Platform(int x, int y, int w, int h) {
        super(x, y, w, h);
        paint.setColor(Color.BLACK);
        tag = Tag.PLATFORM;
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
        Lemon lemon = (Lemon) masterClass;
        lemon.isColliding = true;
        lemon.dy = 0;

        if (lemon.y > this.y) {
            if (lemon.x < this.x)
                lemon.direction = -1;
            else
                lemon.direction = 1;
        }
    }
}
