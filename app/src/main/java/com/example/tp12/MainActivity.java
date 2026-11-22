package com.example.tp12;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

/**
 * MainActivity demonstrating device sensor detection using SensorManager.getSensorList(Sensor.TYPE_ALL),
 * real-time monitoring via SensorEventListener, ambient temperature tracking, and
 * lifecycle-aware listener registration in onResume() and unregistration in onPause().
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;

    // Ambient Temperature Sensor
    private Sensor ambientTempSensor;
    private TextView tvAmbientStatusBadge;
    private TextView tvAmbientTempValue;

    // Real-Time Selected Sensor Monitoring
    private Sensor selectedSensor;
    private TextView tvSelectedSensorName;
    private TextView tvSelectedSensorBadge;
    private TextView tvSelectedSensorHint;
    private TextView tvRealTimeValues;

    // Available Sensors List
    private TextView tvAvailableSensorsHeader;
    private RecyclerView recyclerViewSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle Status Bar Window Insets for comfortable top spacing
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainScroll), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            View mainLayout = findViewById(R.id.mainLayout);
            if (mainLayout != null) {
                mainLayout.setPadding(
                        mainLayout.getPaddingLeft(),
                        systemBars.top + 16,
                        mainLayout.getPaddingRight(),
                        systemBars.bottom + 16
                );
            }
            return insets;
        });

        // Initialize UI Views
        initViews();

        // Initialize SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            setupAmbientTemperatureSensor();
            setupAvailableSensorsList();
        } else {
            showSensorManagerError();
        }
    }

    private void initViews() {
        tvAmbientStatusBadge = findViewById(R.id.tvAmbientStatusBadge);
        tvAmbientTempValue = findViewById(R.id.tvAmbientTempValue);

        tvSelectedSensorName = findViewById(R.id.tvSelectedSensorName);
        tvSelectedSensorBadge = findViewById(R.id.tvSelectedSensorBadge);
        tvSelectedSensorHint = findViewById(R.id.tvSelectedSensorHint);
        tvRealTimeValues = findViewById(R.id.tvRealTimeValues);

        tvAvailableSensorsHeader = findViewById(R.id.tvAvailableSensorsHeader);
        recyclerViewSensors = findViewById(R.id.recyclerViewSensors);
    }

    /**
     * Checks if the ambient temperature sensor (TYPE_AMBIENT_TEMPERATURE) is available on the device.
     */
    private void setupAmbientTemperatureSensor() {
        ambientTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (ambientTempSensor != null) {
            tvAmbientStatusBadge.setText(R.string.sensor_status_active);
            tvAmbientStatusBadge.setTextColor(getColor(R.color.status_active));
            tvAmbientStatusBadge.setBackgroundResource(R.drawable.badge_active);
            tvAmbientTempValue.setText(R.string.ambient_temp_waiting);
        } else {
            tvAmbientStatusBadge.setText(R.string.sensor_status_unavailable);
            tvAmbientStatusBadge.setTextColor(getColor(R.color.status_unavailable));
            tvAmbientStatusBadge.setBackgroundResource(R.drawable.badge_unavailable);
            tvAmbientTempValue.setText(R.string.ambient_temp_unavailable);
        }
    }

    /**
     * Retrieves all device sensors using SensorManager.getSensorList(Sensor.TYPE_ALL)
     * and populates the RecyclerView adapter.
     */
    private void setupAvailableSensorsList() {
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        int count = sensorList != null ? sensorList.size() : 0;
        tvAvailableSensorsHeader.setText(getString(R.string.available_sensors_header, count));

        recyclerViewSensors.setLayoutManager(new LinearLayoutManager(this));
        SensorAdapter sensorAdapter = new SensorAdapter(sensorList, (sensor, position) -> selectSensor(sensor));
        recyclerViewSensors.setAdapter(sensorAdapter);

        // Automatically select the first sensor by default if available
        if (sensorList != null && !sensorList.isEmpty()) {
            sensorAdapter.setSelectedPosition(0);
            selectSensor(sensorList.get(0));
        } else {
            updateSelectedSensorState(false, getString(R.string.no_sensors_found));
        }
    }

    /**
     * Updates the selected sensor for live monitoring and re-registers listeners.
     */
    private void selectSensor(Sensor sensor) {
        // Unregister previous selected sensor listener first
        if (selectedSensor != null) {
            sensorManager.unregisterListener(this, selectedSensor);
        }

        selectedSensor = sensor;

        if (selectedSensor != null) {
            tvSelectedSensorName.setText(selectedSensor.getName());
            tvSelectedSensorHint.setText(String.format("Type: %s | Vendor: %s",
                    SensorUtils.getSensorTypeName(selectedSensor.getType()),
                    selectedSensor.getVendor()));

            // Register listener for newly selected sensor
            boolean success = sensorManager.registerListener(
                    this,
                    selectedSensor,
                    SensorManager.SENSOR_DELAY_UI
            );

            if (success) {
                updateSelectedSensorState(true, "Receiving real-time sensor updates...");
            } else {
                updateSelectedSensorState(false, "Unable to register listener for this sensor.");
            }
        }
    }

    private void updateSelectedSensorState(boolean isActive, String initialValueText) {
        if (isActive) {
            tvSelectedSensorBadge.setText(R.string.sensor_status_active);
            tvSelectedSensorBadge.setTextColor(getColor(R.color.status_active));
            tvSelectedSensorBadge.setBackgroundResource(R.drawable.badge_active);
        } else {
            tvSelectedSensorBadge.setText(R.string.sensor_status_unavailable);
            tvSelectedSensorBadge.setTextColor(getColor(R.color.status_unavailable));
            tvSelectedSensorBadge.setBackgroundResource(R.drawable.badge_unavailable);
        }
        tvRealTimeValues.setText(initialValueText);
    }

    private void showSensorManagerError() {
        tvAmbientStatusBadge.setText(R.string.sensor_status_unavailable);
        tvAmbientStatusBadge.setBackgroundResource(R.drawable.badge_unavailable);
        tvAmbientTempValue.setText(R.string.sensor_manager_unavailable);
        updateSelectedSensorState(false, "SensorManager unavailable.");
    }

    /**
     * Called when sensor values change. Handles real-time value updates for both
     * the Ambient Temperature sensor and the selected target sensor.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null || event.sensor == null) {
            return;
        }

        int sensorType = event.sensor.getType();

        // Update Ambient Temperature display
        if (ambientTempSensor != null && sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float tempCelsius = event.values[0];
            tvAmbientTempValue.setText(String.format(Locale.US, "Current Temperature: %.1f °C", tempCelsius));
        }

        // Update Selected Sensor Real-time display
        if (selectedSensor != null && sensorType == selectedSensor.getType()) {
            String formattedValues = SensorUtils.formatSensorValues(sensorType, event.values);
            tvRealTimeValues.setText(formattedValues);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Optional accuracy callback
    }

    /**
     * Register sensor listeners in onResume() to start receiving real-time updates.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            // Register ambient temperature listener
            if (ambientTempSensor != null) {
                sensorManager.registerListener(
                        this,
                        ambientTempSensor,
                        SensorManager.SENSOR_DELAY_NORMAL
                );
            }

            // Register selected sensor listener
            if (selectedSensor != null) {
                sensorManager.registerListener(
                        this,
                        selectedSensor,
                        SensorManager.SENSOR_DELAY_UI
                );
            }
        }
    }

    /**
     * Unregister sensor listeners in onPause() to save battery power and prevent memory leaks.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            // Unregister listener for all sensors
            sensorManager.unregisterListener(this);
        }
    }
}