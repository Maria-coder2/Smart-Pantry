package com.example.smartpantry.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.models.PantryItem;
import com.example.smartpantry.models.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "quantity REAL NOT NULL CHECK(quantity > 0)," +
                "unit TEXT NOT NULL," +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE," +
                "instructions TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "recipe_id INTEGER NOT NULL," +
                "ingredient_name TEXT NOT NULL," +
                "required_quantity REAL NOT NULL," +
                "unit TEXT NOT NULL," +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id))");

        db.execSQL("CREATE TABLE settings (" +
                "setting_key TEXT PRIMARY KEY," +
                "setting_value TEXT NOT NULL)");

        db.execSQL("INSERT INTO settings VALUES ('expiry_alerts', 'true')");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        // Add database migrations here when the schema changes.
    }

    // CREATE: Add a pantry ingredient.
    public long addPantryItem(String name, double quantity,
                              String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", normalizeName(name));
        values.put("quantity", quantity);
        values.put("unit", normalizeUnit(unit));
        values.put("expiry_date", expiryDate);

        return db.insert("pantry_items", null, values);
    }

    // READ: Retrieve all pantry ingredients.
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                "pantry_items", null, null, null,
                null, null, "name ASC")) {

            while (cursor.moveToNext()) {
                items.add(new PantryItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))
                ));
            }
        }

        return items;
    }

    // UPDATE: Change an existing pantry ingredient.
    public int updatePantryItem(int id, String name, double quantity,
                                String unit, String expiryDate) {
        ContentValues values = new ContentValues();
        values.put("name", normalizeName(name));
        values.put("quantity", quantity);
        values.put("unit", normalizeUnit(unit));
        values.put("expiry_date", expiryDate);

        return getWritableDatabase().update(
                "pantry_items", values, "id = ?",
                new String[]{String.valueOf(id)});
    }

    // DELETE: Remove an ingredient.
    public int deletePantryItem(int id) {
        return getWritableDatabase().delete(
                "pantry_items", "id = ?",
                new String[]{String.valueOf(id)});
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        try (Cursor cursor = getReadableDatabase().query(
                "recipes", null, null, null,
                null, null, "name ASC")) {

            while (cursor.moveToNext()) {
                recipes.add(new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("instructions"))
                ));
            }
        }

        return recipes;
    }

    public Recipe getRecipe(int recipeId) {
        try (Cursor cursor = getReadableDatabase().query(
                "recipes", null, "id = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                return new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("instructions"))
                );
            }
        }

        return null;
    }

    public static class RequiredIngredient {
        public final String name;
        public final double quantity;
        public final String unit;

        public RequiredIngredient(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }

    public List<RequiredIngredient> getRequiredIngredients(int recipeId) {
        List<RequiredIngredient> ingredients = new ArrayList<>();

        try (Cursor cursor = getReadableDatabase().query(
                "recipe_ingredients", null, "recipe_id = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, "ingredient_name ASC")) {

            while (cursor.moveToNext()) {
                ingredients.add(new RequiredIngredient(
                        cursor.getString(cursor.getColumnIndexOrThrow(
                                "ingredient_name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(
                                "required_quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit"))
                ));
            }
        }

        return ingredients;
    }

    public boolean getExpiryAlertsEnabled() {
        try (Cursor cursor = getReadableDatabase().query(
                "settings", new String[]{"setting_value"},
                "setting_key = ?", new String[]{"expiry_alerts"},
                null, null, null)) {

            return cursor.moveToFirst()
                    && Boolean.parseBoolean(cursor.getString(0));
        }
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        ContentValues values = new ContentValues();
        values.put("setting_value", String.valueOf(enabled));

        getWritableDatabase().update(
                "settings", values, "setting_key = ?",
                new String[]{"expiry_alerts"});
    }

    public static String normalizeName(String name) {
        String result = name.trim().toLowerCase(Locale.ROOT);

        if (result.endsWith("ies") && result.length() > 4) {
            result = result.substring(0, result.length() - 3) + "y";
        } else if (result.endsWith("oes") && result.length() > 4) {
            result = result.substring(0, result.length() - 2);
        } else if (result.endsWith("s")
                && !result.endsWith("ss")
                && result.length() > 3) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    public static String normalizeUnit(String unit) {
        String result = unit.trim().toLowerCase(Locale.ROOT);

        switch (result) {
            case "pieces":
            case "piece":
            case "pcs":
            case "pc":
                return "piece";
            case "grams":
            case "gram":
                return "g";
            case "kilograms":
            case "kilogram":
                return "kg";
            case "millilitres":
            case "milliliters":
            case "millilitre":
            case "milliliter":
                return "ml";
            case "litres":
            case "liters":
            case "litre":
            case "liter":
                return "l";
            case "tablespoons":
                return "tbsp";
            case "teaspoons":
                return "tsp";
            default:
                return result;
        }
    }


    private void seedRecipes(SQLiteDatabase db) {
        String[][] data = {
                {"Mushroom Omelette", "Slice mushrooms, sauté briefly, add beaten eggs and cook.",
                        "egg:2:piece", "mushroom:50:g", "butter:5:g", "salt:1:g"},
                {"Spinach Scramble", "Wilt spinach in a pan, pour in beaten eggs and scramble.",
                        "egg:2:piece", "spinach:40:g", "salt:1:g"},
                {"Cucumber Yogurt Salad", "Dice cucumber and stir into yogurt with a pinch of salt.",
                        "cucumber:1:piece", "yogurt:150:g", "salt:1:g"},
                {"Avocado Toast", "Mash avocado with lemon juice and spread on toasted bread.",
                        "avocado:1:piece", "bread:2:piece", "lemon:1:piece"},
                {"Peanut Butter Banana Toast", "Toast bread, spread peanut butter, top with sliced banana.",
                        "bread:2:piece", "peanut_butter:20:g", "banana:1:piece"},
                {"Tuna Sandwich", "Mix tuna with mayonnaise and spread between slices of bread.",
                        "tuna:80:g", "mayonnaise:15:g", "bread:2:piece"},
                {"Strawberry Smoothie", "Blend strawberries, yogurt and milk until smooth.",
                        "strawberry:100:g", "yogurt:100:g", "milk:100:ml"},
                {"Oatmeal with Apple", "Cook oats in milk, top with chopped apple and honey.",
                        "oats:50:g", "milk:200:ml", "apple:1:piece", "honey:10:g"},
                {"Sweet Potato Mash", "Boil sweet potatoes, mash with butter and salt.",
                        "sweet_potato:2:piece", "butter:15:g", "salt:1:g"},
                {"Vegetable Rice", "Fry cooked rice with peas, bell pepper and salt.",
                        "rice:200:g", "peas:50:g", "bell_pepper:1:piece", "salt:1:g"},
                {"Cucumber Tomato Salad", "Chop cucumber and tomato, dress with olive oil and salt.",
                        "cucumber:1:piece", "tomato:2:piece", "olive_oil:10:ml", "salt:1:g"},
                {"Pesto Pasta", "Cook pasta and toss with pesto.",
                        "pasta:100:g", "pesto:30:g"},
                {"Creamy Mushroom Pasta", "Cook pasta, sauté mushrooms and garlic, stir in cream.",
                        "pasta:100:g", "mushroom:60:g", "garlic:1:piece", "cream:50:ml"},
                {"Zucchini Omelette", "Slice zucchini, combine with beaten eggs and cook.",
                        "zucchini:1:piece", "egg:2:piece", "salt:1:g"},
                {"Oat Pancakes", "Mix oats, egg and milk into a batter, then cook in a pan.",
                        "oats:60:g", "egg:1:piece", "milk:100:ml"},
                {"Grilled Cheese Tomato", "Layer cheese and sliced tomato between bread and grill.",
                        "bread:2:piece", "cheese:30:g", "tomato:1:piece", "butter:5:g"},
                {"Orange Juice", "Squeeze oranges and serve cold.",
                        "orange:3:piece"},
                {"Roasted Corn", "Brush corn with oil, roast and sprinkle with salt.",
                        "corn:1:piece", "oil:5:ml", "salt:1:g"},
                {"Pepper Omelette", "Mix chopped bell pepper with beaten eggs and cook.",
                        "bell_pepper:1:piece", "egg:2:piece", "salt:1:g"},
                {"Berry Yogurt Bowl", "Top yogurt with sliced strawberries and honey.",
                        "yogurt:150:g", "strawberry:80:g", "honey:10:g"}
        };

        for (String[] row : data) {
            ContentValues recipe = new ContentValues();
            recipe.put("name", row[0]);
            recipe.put("instructions", row[1]);

            long recipeId = db.insert("recipes", null, recipe);

            if (recipeId == -1) {
                continue;
            }

            for (int i = 2; i < row.length; i++) {
                String[] parts = row[i].split(":");

                ContentValues ingredient = new ContentValues();
                ingredient.put("recipe_id", recipeId);
                ingredient.put("ingredient_name",
                        normalizeName(parts[0]));
                ingredient.put("required_quantity",
                        Double.parseDouble(parts[1]));
                ingredient.put("unit", normalizeUnit(parts[2]));

                db.insert("recipe_ingredients", null, ingredient);
            }
        }
    }

}

