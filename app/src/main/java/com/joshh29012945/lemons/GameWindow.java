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

        //Gets the intent to extract the data passed to it
        Intent intent = getIntent();
        String level = intent.getStringExtra("level");
        String intentExternal = intent.getStringExtra("externalFile");
        // isExternal determins whether the level file in inside or outside the APK
        boolean isExternal = Boolean.parseBoolean(intentExternal);

        String file = null;
        String name = "";
        //gets the file name and tbe name of the level
        switch (level) {
            case "Level 1":
                file = "Level1.txt";
                name = "Level 1";
                break;
            case "Level 2":
                file = "Level2.txt";
                name = "Level 2";
                break;
            case "Level 3":
                file = "Level3.txt";
                name = "Level 3";
                break;
            default:
                // if it is an external file, the data held in
                // file becomes the data read from the file
                if (isExternal)
                    file = level;
                break;
        }

        //Create a new game
        Game game = new Game(this, file, isExternal, name);
        game.setBackgroundColor(Color.WHITE);

        // the view to the game
        setContentView(game);

        //start the thread
        Thread thread = new Thread(game);
        thread.start();
    }
}