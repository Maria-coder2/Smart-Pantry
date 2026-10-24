package com.example.smartpantry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.database.DatabaseHelper;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper database;
    private Switch expirySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        database = new DatabaseHelper(this);
        expirySwitch = findViewById(R.id.switchExpiryAlerts);
        Button back = findViewById(R.id.btnBackSettings);

        expirySwitch.setChecked(database.getExpiryAlertsEnabled());

        expirySwitch.setOnCheckedChangeListener(
                (CompoundButton button, boolean enabled) ->
                        database.setExpiryAlertsEnabled(enabled));

        back.setOnClickListener(v -> finish());
    }
}