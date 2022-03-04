package com.joshh29012945.lemons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    String[] items;
    String[] descriptions;

    public ItemAdapter(Context c, String[] i, String[] d) {
        items = i;
        descriptions = d;
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View v = mInflator.inflate(R.layout.activity_level_list, null);
        TextView nameTextView = (TextView) v.findViewById(R.id.name_text_view);
        TextView descriptionTextView = (TextView) v.findViewById(R.id.description_view);

        String name = items[i];
        String desc = descriptions[i];

        nameTextView.setText(name);
        descriptionTextView.setText(desc);

        return v;
    }
}


