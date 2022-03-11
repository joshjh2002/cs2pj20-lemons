package com.joshh29012945.lemons;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
        if (requestCode == PICK_FILE_RESULT_CODE) {
            if (resultCode == -1) {
                Uri fileUri = data.getData();
                String filePath = fileUri.getPath();
                Button pickBtn = findViewById(R.id.LoadFileBtn);
                pickBtn.setText(filePath);
            }
        }
    }
}