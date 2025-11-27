package com.example.pomoz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pomoz.R;

import java.util.List;

public class StarAdapter extends ArrayAdapter<Integer> {

    public StarAdapter(Context context, List<Integer> stars) {
        super(context, 0, stars);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createStarView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createStarView(position, convertView, parent);
    }

    private View createStarView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_star, parent, false
            );
        }

        TextView tv = convertView.findViewById(R.id.textStars);

        int count = getItem(position);
        tv.setText(generateStars(count));

        return convertView;
    }

    private String generateStars(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append("★");
        return sb.toString();
    }
}
