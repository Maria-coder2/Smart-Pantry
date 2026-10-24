package com.example.smartpantry.database;

import com.example.smartpantry.models.PantryItem;
import com.example.smartpantry.models.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecipeMatcher {

    private final DatabaseHelper database;

    public RecipeMatcher(DatabaseHelper database) {
        this.database = database;
    }

    private String dimension(String unit) {
        String u = DatabaseHelper.normalizeUnit(unit);

        switch (u) {
            case "kg":
            case "g":
                return "mass";
            case "l":
            case "ml":
            case "tbsp":
            case "tsp":
                return "volume";
            case "piece":
                return "count";
            default:
                return "unit:" + u;
        }
    }

    private double toBaseQuantity(double quantity, String unit) {
        String u = DatabaseHelper.normalizeUnit(unit);

        switch (u) {
            case "kg": return quantity * 1000;
            case "g": return quantity;
            case "l": return quantity * 1000;
            case "ml": return quantity;
            case "tbsp": return quantity * 15;
            case "tsp": return quantity * 5;
            default: return quantity;
        }
    }

    public List<Recipe> getSuggestedRecipes() {
        List<PantryItem> pantry = database.getAllPantryItems();
        List<Recipe> recipes = database.getAllRecipes();

        Map<String, Double> available = new HashMap<>();

        for (PantryItem item : pantry) {
            String name = DatabaseHelper.normalizeName(item.getName());
            String unit = DatabaseHelper.normalizeUnit(item.getUnit());
            String key = name + "|" + dimension(unit);

            available.merge(
                    key,
                    toBaseQuantity(item.getQuantity(), unit),
                    Double::sum
            );
        }

        recipes.removeIf(recipe -> {
            List<DatabaseHelper.RequiredIngredient> required =
                    database.getRequiredIngredients(recipe.getId());

            if (required.isEmpty()) {
                return true;
            }

            for (DatabaseHelper.RequiredIngredient ingredient : required) {
                String name =
                        DatabaseHelper.normalizeName(ingredient.name);
                String unit =
                        DatabaseHelper.normalizeUnit(ingredient.unit);
                String key = name + "|" + dimension(unit);

                double amount = available.getOrDefault(key, 0.0);
                double needed =
                        toBaseQuantity(ingredient.quantity, unit);

                if (amount + 0.000001 < needed) {
                    return true;
                }
            }

            return false;
        });

        return recipes;
    }
}
