package com.example.smartpantry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.models.Recipe;

import java.util.List;
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView name = findViewById(R.id.txtRecipeName);
        TextView ingredients = findViewById(R.id.txtRecipeIngredients);
        TextView instructions = findViewById(R.id.txtRecipeInstructions);
        Button back = findViewById(R.id.btnBackRecipes);

        back.setOnClickListener(v -> finish());

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        DatabaseHelper database = new DatabaseHelper(this);
        Recipe recipe = database.getRecipe(recipeId);

        if (recipe == null) {
            Toast.makeText(this, "Recipe not found",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        name.setText(recipe.getName());
        instructions.setText(recipe.getInstructions());

        List<DatabaseHelper.RequiredIngredient> required =
                database.getRequiredIngredients(recipeId);

        StringBuilder text = new StringBuilder();

        for (DatabaseHelper.RequiredIngredient item : required) {
            text.append("• ")
                    .append(item.name)
                    .append(" — ")
                    .append(item.quantity)
                    .append(" ")
                    .append(item.unit)
                    .append("\n");
        }

        ingredients.setText(text.toString());
    }
}
