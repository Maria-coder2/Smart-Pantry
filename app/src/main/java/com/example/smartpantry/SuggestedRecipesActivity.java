package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.adapters.RecipeAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.database.RecipeMatcher;
import com.example.smartpantry.models.Recipe;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper database;
    private ListView recipeList;
    private TextView emptyMessage;
    private List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        database = new DatabaseHelper(this);
        recipeList = findViewById(R.id.listRecipes);
        emptyMessage = findViewById(R.id.txtNoRecipes);

        Button back = findViewById(R.id.btnBackToPantry);
        back.setOnClickListener(v -> finish());

        recipeList.setOnItemClickListener((parent, view, position, id) -> {
            Recipe recipe = recipes.get(position);

            Intent intent = new Intent(this,
                    RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        RecipeMatcher matcher = new RecipeMatcher(database);
        recipes = matcher.getSuggestedRecipes();

        recipeList.setAdapter(new RecipeAdapter(this, recipes));

        boolean hasRecipes = !recipes.isEmpty();
        recipeList.setVisibility(hasRecipes ? View.VISIBLE : View.GONE);
        emptyMessage.setVisibility(hasRecipes ? View.GONE : View.VISIBLE);
    }
}
