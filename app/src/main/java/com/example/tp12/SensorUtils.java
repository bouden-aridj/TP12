package com.example.tp12;

import android.hardware.Sensor;
import java.util.Locale;

/**
 * Helper utility to map Android sensor types to readable names and format live readings.
 */
public class SensorUtils {

    /**
     * Converts a raw Sensor integer type into a readable title string.
     */
    @SuppressWarnings("deprecation")
    public static String getSensorTypeName(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                return "Accelerometer";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "Magnetic Field";
            case Sensor.TYPE_ORIENTATION:
                return "Orientation (Deprecated)";
            case Sensor.TYPE_GYROSCOPE:
                return "Gyroscope";
            case Sensor.TYPE_LIGHT:
                return "Light Sensor";
            case Sensor.TYPE_PRESSURE:
                return "Barometer / Pressure";
            case Sensor.TYPE_TEMPERATURE:
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "Ambient Temperature";
            case Sensor.TYPE_PROXIMITY:
                return "Proximity Sensor";
            case Sensor.TYPE_GRAVITY:
                return "Gravity Sensor";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "Linear Acceleration";
            case Sensor.TYPE_ROTATION_VECTOR:
                return "Rotation Vector";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "Relative Humidity";
            case Sensor.TYPE_STEP_COUNTER:
                return "Step Counter";
            case Sensor.TYPE_STEP_DETECTOR:
                return "Step Detector";
            case Sensor.TYPE_HEART_RATE:
                return "Heart Rate Sensor";
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return "Game Rotation Vector";
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                return "Geomagnetic Rotation Vector";
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                return "Significant Motion";
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                return "Accelerometer (Uncalibrated)";
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return "Gyroscope (Uncalibrated)";
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return "Magnetic Field (Uncalibrated)";
            case Sensor.TYPE_POSE_6DOF:
                return "Pose 6DOF";
            case Sensor.TYPE_STATIONARY_DETECT:
                return "Stationary Detect";
            case Sensor.TYPE_MOTION_DETECT:
                return "Motion Detect";
            case Sensor.TYPE_HEART_BEAT:
                return "Heart Beat";
            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                return "Low Latency Off-body Detect";
            default:
                return "Type #" + sensorType;
        }
    }

    /**
     * Formats floating-point sensor values based on sensor type.
     */
    @SuppressWarnings("deprecation")
    public static String formatSensorValues(int sensorType, float[] values) {
        if (values == null || values.length == 0) {
            return "No values received";
        }

        StringBuilder sb = new StringBuilder();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_GRAVITY:
            case Sensor.TYPE_LINEAR_ACCELERATION:
                if (values.length >= 3) {
                    sb.append(String.format(Locale.US, "X Axis: %+.3f m/s²\n", values[0]));
                    sb.append(String.format(Locale.US, "Y Axis: %+.3f m/s²\n", values[1]));
                    sb.append(String.format(Locale.US, "Z Axis: %+.3f m/s²", values[2]));
                } else {
                    formatGenericValues(sb, values);
                }
                break;

            case Sensor.TYPE_GYROSCOPE:
                if (values.length >= 3) {
                    sb.append(String.format(Locale.US, "X Axis (Pitch): %+.3f rad/s\n", values[0]));
                    sb.append(String.format(Locale.US, "Y Axis (Roll):  %+.3f rad/s\n", values[1]));
                    sb.append(String.format(Locale.US, "Z Axis (Yaw):   %+.3f rad/s", values[2]));
                } else {
                    formatGenericValues(sb, values);
                }
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                if (values.length >= 3) {
                    sb.append(String.format(Locale.US, "X Axis: %+.2f µT\n", values[0]));
                    sb.append(String.format(Locale.US, "Y Axis: %+.2f µT\n", values[1]));
                    sb.append(String.format(Locale.US, "Z Axis: %+.2f µT", values[2]));
                } else {
                    formatGenericValues(sb, values);
                }
                break;

            case Sensor.TYPE_LIGHT:
                sb.append(String.format(Locale.US, "Illuminance: %.1f lx", values[0]));
                break;

            case Sensor.TYPE_PROXIMITY:
                sb.append(String.format(Locale.US, "Distance: %.1f cm", values[0]));
                break;

            case Sensor.TYPE_PRESSURE:
                sb.append(String.format(Locale.US, "Pressure: %.2f hPa", values[0]));
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
            case Sensor.TYPE_TEMPERATURE:
                sb.append(String.format(Locale.US, "Temperature: %.1f °C", values[0]));
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sb.append(String.format(Locale.US, "Humidity: %.1f %%", values[0]));
                break;

            case Sensor.TYPE_STEP_COUNTER:
                sb.append(String.format(Locale.US, "Total Steps: %.0f", values[0]));
                break;

            default:
                formatGenericValues(sb, values);
                break;
        }

        return sb.toString();
    }

    private static void formatGenericValues(StringBuilder sb, float[] values) {
        for (int i = 0; i < values.length; i++) {
            sb.append(String.format(Locale.US, "Value[%d]: %+.4f", i, values[i]));
            if (i < values.length - 1) {
                sb.append("\n");
            }
        }
    }
}