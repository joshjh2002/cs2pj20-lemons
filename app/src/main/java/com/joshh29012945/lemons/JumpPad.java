package com.joshh29012945.lemons;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;

/**
 * When collided with, a lemon will jump
 */
public class JumpPad extends Object {
    /**
     * Stores image for all jump pads
     */
    public static Bitmap image;
    /**
     * Stores jump sound
     */
    public static MediaPlayer jumpSound;

    /**
     * The height that the trampoline will bounce the player to
     */
    float jump_height;

    public JumpPad(int x, int y, float h) {
        super(x, y, 96, 55);
        paint.setColor(Color.BLUE);
        tag = Tag.JUMP;
        jump_height = h;
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
    public void OnTouch() {

    }

    @Override
    public void OnCollide(MasterClass masterClass) {
        Lemon lemon = (Lemon) masterClass;
        if (lemon.jumping > 0)
            jumpSound.start();
        lemon.jump(jump_height);
    }

    @Override
    protected void OnButtonPressed() {

    }

    @Override
    protected void OnButtonPressedExit() {

    }
}
