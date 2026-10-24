package com.example.smartpantry.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.smartpantry.models.PantryItem;

import java.util.List;
import java.util.Locale;


public class PantryAdapter extends ArrayAdapter<PantryItem> {

    public PantryAdapter(Activity context, List<PantryItem> items) {
        super(context, android.R.layout.simple_list_item_2, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        PantryItem item = getItem(position);

        TextView title = view.findViewById(android.R.id.text1);
        TextView detail = view.findViewById(android.R.id.text2);

        if (item != null) {
            title.setText(item.getName());
            title.setTypeface(null, Typeface.BOLD);

            detail.setText(String.format(
                    Locale.getDefault(),
                    "Quantity: %s %s  |  Expiry: %s",
                    item.getQuantity(),
                    item.getUnit(),
                    item.getExpiryDate() == null
                            || item.getExpiryDate().isEmpty()
                            ? "Not set" : item.getExpiryDate()
            ));
        }

        return view;
    }
}
