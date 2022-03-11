package com.joshh29012945.lemons;

import android.graphics.Bitmap;

/**
 * Functions the same as a standard lemon but will stop moving when tapped
 */
public class StopperLemon extends StandardLemon {
    public static Bitmap image;
    float touchCoolDown = 0.3f;
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
        if (touchCoolDown <= touchRunningTotal) {
            this.canMove = !this.canMove;
            touchRunningTotal = 0f;
        }
    }
}
