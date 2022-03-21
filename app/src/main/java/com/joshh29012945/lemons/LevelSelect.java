package com.joshh29012945.lemons;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * An intent to select a level to play
 */
public class LevelSelect extends AppCompatActivity {
    ListView myListView;
    String[] name;
    String[] descriptions;
    String[] highScore;
    String currentScore = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();


        setContentView(R.layout.activity_level_select);

        Resources res = getResources();
        myListView = findViewById(R.id.level_list_view);
        name = res.getStringArray(R.array.level_name);

        //myListView.setAdapter(new ArrayAdapter<String>(this, R.layout.my_listView_detail, items)); we will update a better version
        descriptions = res.getStringArray(R.array.level_description);

        ItemAdapter itemAdapter = new ItemAdapter(this, name, descriptions);
        myListView.setAdapter(itemAdapter);

        Context context = this;

        myListView.setOnItemClickListener((parent, view, i, l) -> {
            Intent switchActivityIntent = new Intent(context, GameWindow.class);
            switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            switchActivityIntent.putExtra("level", name[i]);
            startActivity(switchActivityIntent);

        });

        Button mainMenu = findViewById(R.id.to_main_menu);
        mainMenu.setOnClickListener(e -> {
            finish();
            Intent switchActivityIntent = new Intent(this, MainActivity.class);
            switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(switchActivityIntent);
        });
    }
}