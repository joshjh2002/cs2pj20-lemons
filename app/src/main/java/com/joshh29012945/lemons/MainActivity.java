package com.joshh29012945.lemons;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
    public static final int PICK_FILE_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);


        Button startBtn = findViewById(R.id.StartGameBtn);
        startBtn.setOnClickListener(e -> {
            finish();
            Intent switchActivityIntent = new Intent(this, LevelSelect.class);
            switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(switchActivityIntent);
        });


        Button pickBtn = findViewById(R.id.LoadFileBtn);
        pickBtn.setOnClickListener(e -> {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            startActivityForResult(chooseFile, PICK_FILE_RESULT_CODE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_RESULT_CODE && resultCode == RESULT_OK) {
            Uri content_describer = data.getData();
            BufferedReader reader = null;
            try {
                // open the user-picked file for reading:
                InputStream in = getContentResolver().openInputStream(content_describer);
                // now read the content:
                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }
                // Do something with the content in
                Button pickBtn = findViewById(R.id.LoadFileBtn);
                pickBtn.setText(builder.toString());

                Intent switchActivityIntent = new Intent(this, GameWindow.class);
                switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                switchActivityIntent.putExtra("level", builder.toString());
                switchActivityIntent.putExtra("externalFile", "true");
                startActivity(switchActivityIntent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}