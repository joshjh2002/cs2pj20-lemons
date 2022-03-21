package com.joshh29012945.lemons;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;

public class Button extends Object {

    public static Bitmap image;
    public static Bitmap image_pressed;
    public static MediaPlayer clickEffect;

    /**
     * Holds a reference to the object that the button is linked to
     */
    private Object linked_object;

    /**
     * Holds a reference to the lemon currently pressing the button
     */
    private Lemon active_lemon;

    public Button(int x, int y, Object linked_object) {
        super(x, y, 64, 16);
        this.linked_object = linked_object;
        tag = Tag.BUTTON;
        paint.setColor(Color.rgb(255, 150, 150));
    }

    public boolean isPressed() {
        return active_lemon != null;
    }

    @Override
    public void Update() {
        // If something previously collided with the button
        if (active_lemon != null) {
            Rect lemonRect = new Rect((int) active_lemon.x, (int) active_lemon.y,
                    (int) active_lemon.x + active_lemon.w, (int) active_lemon.y + active_lemon.h);
            Rect objectRect = new Rect((int) this.x, (int) this.y,
                    (int) this.x + this.w, (int) this.y + this.h);

            // Check if they are still colliding
            if (!objectRect.intersect(lemonRect)) {
                // If the are no longer colliding, reset the object the button is linked to
                active_lemon = null;
                linked_object.OnButtonPressedExit();

            }
        }
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
        if (active_lemon == null)
            clickEffect.start();
        active_lemon = (Lemon) masterClass;
        linked_object.OnButtonPressed();
    }

    @Override
    protected void OnButtonPressed() {

    }

    @Override
    protected void OnButtonPressedExit() {

    }
}
