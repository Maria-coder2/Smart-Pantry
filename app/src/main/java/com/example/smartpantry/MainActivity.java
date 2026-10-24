package com.example.smartpantry;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.smartpantry.adapters.PantryAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.models.PantryItem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DatabaseHelper database;
    private PantryAdapter adapter;
    private final List<PantryItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DatabaseHelper(this);

        Button add = findViewById(R.id.btnAddIngredient);
        Button recipes = findViewById(R.id.btnSuggestedRecipes);
        Button settings = findViewById(R.id.btnSettings);
        ListView list = findViewById(R.id.listPantry);

        adapter = new PantryAdapter(this, items);
        list.setAdapter(adapter);

        add.setOnClickListener(v ->
                startActivity(new Intent(this,
                        AddEditIngredientActivity.class)));

        recipes.setOnClickListener(v ->
                startActivity(new Intent(this,
                        SuggestedRecipesActivity.class)));

        settings.setOnClickListener(v ->
                startActivity(new Intent(this,
                        SettingsActivity.class)));

        list.setOnItemClickListener((parent, view, position, id) -> {
            PantryItem item = items.get(position);

            Intent intent = new Intent(this,
                    AddEditIngredientActivity.class);
            intent.putExtra("item_id", item.getId());
            startActivity(intent);
        });

        list.setOnItemLongClickListener((parent, view, position, id) -> {
            PantryItem item = items.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Delete ingredient")
                    .setMessage("Delete " + item.getName() + "?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {
                        database.deletePantryItem(item.getId());
                        loadPantry();
                        Toast.makeText(this, "Ingredient deleted",
                                Toast.LENGTH_SHORT).show();
                    })
                    .show();

            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (database != null) {
            loadPantry();
        }
    }

    private void loadPantry() {
        items.clear();
        items.addAll(database.getAllPantryItems());
        adapter.notifyDataSetChanged();
    }
}
