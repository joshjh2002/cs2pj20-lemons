package com.joshh29012945.lemons;

import android.graphics.Paint;

public abstract class Object extends MasterClass {
    Paint paint;
    public Object(int x, int y, int w, int h) {
        super(x, y, w, h);
        paint = new Paint();
    }

    public abstract void OnCollide(MasterClass masterClass);
}
