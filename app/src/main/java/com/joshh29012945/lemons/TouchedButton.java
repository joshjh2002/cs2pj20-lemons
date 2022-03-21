package com.joshh29012945.lemons;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;

public class TouchedButton extends Object {

    /**
     * Holds a reference to the object that the button is linked to
     */
    private Object linked_object;

    private boolean state;

    private float time_since_press;

    public static MediaPlayer clickEffect;

    public TouchedButton(int x, int y, Object linked_object) {
        super(x, y, 100, 100);
        this.linked_object = linked_object;
        tag = Tag.TOUCH_BUTTON;
        state = false;
        time_since_press = 1f;
        paint.setColor(Color.rgb(255, 150, 150));
    }


    @Override
    public void Update() {
        time_since_press += Game.FrameTime();

        if (state) {
            linked_object.OnButtonPressed();
            paint.setColor(Color.GREEN);
        }
        else {
            linked_object.OnButtonPressedExit();
            paint.setColor(Color.RED);
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
        if (time_since_press >= 0.1f) {
            state = !state;
            time_since_press = 0;
            clickEffect.start();
        }
    }

    @Override
    public void OnCollide(MasterClass masterClass) {

    }

    @Override
    protected void OnButtonPressed() {

    }

    @Override
    protected void OnButtonPressedExit() {

    }
}
