package com.example.smartpantry.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.smartpantry.models.Recipe;

import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    public RecipeAdapter(Activity context, List<Recipe> recipes) {
        super(context, android.R.layout.simple_list_item_2, recipes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Recipe recipe = getItem(position);

        TextView name = view.findViewById(android.R.id.text1);
        TextView details = view.findViewById(android.R.id.text2);

        if (recipe != null) {
            name.setText(recipe.getName());
            details.setText("Tap to view ingredients and method");
        }

        return view;
    }
}
