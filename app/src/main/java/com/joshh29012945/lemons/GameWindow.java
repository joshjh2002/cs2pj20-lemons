package com.joshh29012945.lemons;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Loads the game based on the data passed via the intent
 */
public class GameWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();


        Intent intent = getIntent();
        String level = intent.getStringExtra("level");
        String intentExternal = intent.getStringExtra("externalFile");
        boolean isExternal = Boolean.parseBoolean(intentExternal);

        String file = null;

        switch (level) {
            case "Level 1":
                file = "Level1.txt";
                break;
            case "Level 2":
                file = "Level2.txt";
                break;
            case "Level 3":
                file = "Level3.txt";
                break;
            default:
                if (isExternal)
                    file = level;
                break;
        }

        Game game = new Game(this, file, isExternal);
        game.setBackgroundColor(Color.WHITE);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(game);

        Thread thread = new Thread(game);
        thread.start();
    }
}