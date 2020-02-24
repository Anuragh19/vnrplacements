package com.example.vnrplacements;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class textadapter extends ArrayAdapter<String> {
    private Context mContext;
    public textadapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.listviewfile, parent, false);
        }
        TextView company=(TextView)convertView.findViewById(R.id.listviewtextview);
        String string=getItem(position);
        company.setText(string);
        return convertView;
    }
}