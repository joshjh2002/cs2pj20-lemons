package com.joshh29012945.lemons;

import android.graphics.Bitmap;

public abstract class MasterClass {
    public static Bitmap image = null;
    float x, y;
    int w, h;
    Tag tag;
    boolean isDead;

    public MasterClass(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        isDead = false;
        OnCreate();
    }

    public abstract void Update();

    public abstract void OnDeath();

    public abstract void OnCreate();
}
