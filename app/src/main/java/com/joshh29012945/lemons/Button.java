package com.joshh29012945.lemons;

import android.graphics.Color;
import android.graphics.Rect;

public class Button extends Object {
    Object linked_object;
    Lemon touchingLemon;

    public Button(int x, int y, Object linked_object) {
        super(x, y, 64, 16);
        this.linked_object = linked_object;
        tag = Tag.BUTTON;
        paint.setColor(Color.rgb(255, 150,150));
    }

    @Override
    public void Update() {
        if (touchingLemon != null)
        {
            Rect lemonRect = new Rect((int) touchingLemon.x, (int) touchingLemon.y,
                    (int) touchingLemon.x + touchingLemon.w, (int) touchingLemon.y + touchingLemon.h);
            Rect objectRect = new Rect((int) this.x, (int) this.y,
                    (int) this.x + this.w, (int) this.y + this.h);

            if (!objectRect.intersect(lemonRect))
            {
                touchingLemon = null;
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
        Lemon lemon = (Lemon) masterClass;
        touchingLemon = lemon;
        linked_object.OnButtonPressed();
    }

    @Override
    protected void OnButtonPressed() {

    }

    @Override
    protected void OnButtonPressedExit() {

    }
}
