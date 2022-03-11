package com.joshh29012945.lemons;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Screen used to display level completion stats
 */
public class LevelFinished extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();


        setContentView(R.layout.activity_level_finished);

        Intent intent = getIntent();

        String pass_fail = intent.getStringExtra("pass_fail");

        TextView textView = findViewById(R.id.level_finished_text);
        String out = getString(R.string.level_finished, pass_fail);
        textView.setText(out);

        Button mainMenu = findViewById(R.id.returnBtn);
        mainMenu.setOnClickListener(e ->{
            finish();
            Intent switchActivityIntent = new Intent(this, MainActivity.class);
            switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(switchActivityIntent);
        });
    }
}