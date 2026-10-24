package com.example.smartpantry;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.models.PantryItem;

public class AddEditIngredientActivity extends AppCompatActivity {

    private DatabaseHelper database;
    private EditText nameInput, quantityInput, expiryInput;
    private Spinner unitSpinner;
    private int itemId = -1;

    private final String[] units = {
            "piece", "g", "kg", "ml", "l", "tbsp", "tsp"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        database = new DatabaseHelper(this);

        nameInput = findViewById(R.id.inputName);
        quantityInput = findViewById(R.id.inputQuantity);
        expiryInput = findViewById(R.id.inputExpiry);
        unitSpinner = findViewById(R.id.spinnerUnit);

        TextView title = findViewById(R.id.txtFormTitle);
        Button save = findViewById(R.id.btnSaveIngredient);
        Button cancel = findViewById(R.id.btnCancelIngredient);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        itemId = getIntent().getIntExtra("item_id", -1);

        if (itemId != -1) {
            title.setText("Edit Ingredient");
            loadExistingItem();
        }

        save.setOnClickListener(v -> saveIngredient());
        cancel.setOnClickListener(v -> finish());
    }

    private void loadExistingItem() {
        for (PantryItem item : database.getAllPantryItems()) {
            if (item.getId() == itemId) {
                nameInput.setText(item.getName());
                quantityInput.setText(
                        String.valueOf(item.getQuantity()));
                expiryInput.setText(item.getExpiryDate());

                for (int i = 0; i < units.length; i++) {
                    if (units[i].equalsIgnoreCase(item.getUnit())) {
                        unitSpinner.setSelection(i);
                        break;
                    }
                }

                break;
            }
        }
    }

    private void saveIngredient() {
        String name = nameInput.getText().toString().trim();
        String quantityText =
                quantityInput.getText().toString().trim();
        String unit = unitSpinner.getSelectedItem().toString();
        String expiry = expiryInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Enter an ingredient name");
            nameInput.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            quantityInput.setError("Enter a quantity");
            quantityInput.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            quantityInput.setError("Enter a valid number");
            return;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            quantityInput.setError("Quantity must be greater than zero");
            return;
        }

        if (!expiry.isEmpty()
                && !expiry.matches("\\d{4}-\\d{2}-\\d{2}")) {
            expiryInput.setError("Use YYYY-MM-DD");
            return;
        }

        long result;

        if (itemId == -1) {
            result = database.addPantryItem(
                    name, quantity, unit, expiry);
        } else {
            result = database.updatePantryItem(
                    itemId, name, quantity, unit, expiry);
        }

        if (result > 0) {
            Toast.makeText(this, "Ingredient saved",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save ingredient",
                    Toast.LENGTH_LONG).show();
        }
    }
}