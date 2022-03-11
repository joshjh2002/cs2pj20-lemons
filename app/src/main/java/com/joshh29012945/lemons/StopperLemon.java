package com.joshh29012945.lemons;

import android.graphics.Bitmap;

public class StopperLemon extends StandardLemon {
    public static Bitmap image;
    float touchCooldown = 0.1f;
    float touchRunningTotal = 1f;

    public StopperLemon(int x, int y) {
        super(x, y);
        tag = Tag.STOPPER_LEMON;
    }

    @Override
    public void Update() {
        super.Update();
        touchRunningTotal += Game.frame_time;
    }

    @Override
    public void OnTouch() {
        if (touchCooldown <= touchRunningTotal) {
            this.canMove = !this.canMove;
            touchRunningTotal = 0f;
        }
    }
}
