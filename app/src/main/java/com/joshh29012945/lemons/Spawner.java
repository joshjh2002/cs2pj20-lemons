package com.joshh29012945.lemons;

import android.graphics.Color;

public class Spawner extends Object {
    Game currentGame;
    float elapsed_time;
    float spawn_delay;

    public Spawner(int x, int y, Game currentGame, float spawn_delay) {
        super(x, y, 64, 64);
        this.currentGame = currentGame;
        this.tag = Tag.SPAWNER;
        this.paint.setColor(Color.TRANSPARENT);
        this.spawn_delay = spawn_delay;
        this.elapsed_time = spawn_delay;
    }

    @Override
    public void Update() {
        elapsed_time += Game.frame_time;

        if (currentGame.lemonsBuffer.size() > 0 && elapsed_time >= spawn_delay) {
            Lemon lemon = currentGame.lemonsBuffer.get(0);
            lemon.x = this.x;
            lemon.y = this.y;
            currentGame.lemons.add(lemon);
            currentGame.lemonsBuffer.remove(0);
            elapsed_time = 0;
        }
    }

    @Override
    public void OnDeath() {

    }

    @Override
    public void OnCreate() {

    }

    @Override
    public void OnCollide(MasterClass masterClass) {

    }
}
