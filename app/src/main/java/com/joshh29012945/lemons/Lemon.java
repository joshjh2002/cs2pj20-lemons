package com.joshh29012945.lemons;

public abstract class Lemon extends MasterClass {
    double dx, dy = 300;
    Boolean isColliding = false;
    int direction = 1;
    int jumping = 1;
    float timeJumping = 0;
    float jumpHeight = 0;

    public Lemon(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public void Update() {
        if (!isColliding) {
            dy = 300;
            dx = 150;
        } else {
            dx = 200;
        }

        if (jumping == -1)
        {
            timeJumping += Game.frame_time;
            if (timeJumping >= jumpHeight)
            {
                jumping = 1;
            }
        }
        else
        {
            timeJumping = 0;
        }

        if (Game.touching) {
            this.x = Game.x;
            this.y = Game.y;
        }

        this.y += dy * jumping * Game.frame_time;
        this.x += dx * direction * Game.frame_time;
    }

    public void jump(float jumpHeightInSeconds){
        jumping = -1;
        jumpHeight = jumpHeightInSeconds;
    }
}
