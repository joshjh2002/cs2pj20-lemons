package com.joshh29012945.lemons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ItemAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    String[] items;
    String[] descriptions;

    public ItemAdapter(Context c, String[] i, String[] d) {
        items = i;
        descriptions = d;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public java.lang.Object getItem(int i) {
        return items[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //Suppressed the errors
        @SuppressLint({"ViewHolder", "InflateParams"}) View v = mInflater.inflate(R.layout.activity_level_list, null);

        //Gets the relevant text views
        TextView nameTextView = v.findViewById(R.id.name_text_view);
        TextView descriptionTextView = v.findViewById(R.id.description_view);
        TextView highScore = v.findViewById(R.id.highScore);

        //sets the name and description of the current level entry
        String name = items[i];
        String desc = descriptions[i];

        nameTextView.setText(name);
        descriptionTextView.setText(desc);

        // gets the high score of that level and sets the textview to display it
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://lemons-80393-default-rtdb.firebaseio.com/");
        for (int i2 = 0; i2 < name.length(); i2++) {
            DatabaseReference myRef = null;
            myRef = database.getReference(name).child("score");
            myRef.get().addOnCompleteListener(y -> {
                if (y.isSuccessful()) {
                    if (y.getResult().exists()) {
                        highScore.setText("High Score: " + y.getResult().getValue().toString());
                    } else {

                    }
                }

            });
        }

        return v;
    }
}


