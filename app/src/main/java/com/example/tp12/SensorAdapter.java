package com.example.tp12;

import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter displaying available device sensors in cards.
 */
public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    public interface OnSensorClickListener {
        void onSensorClick(Sensor sensor, int position);
    }

    private final List<Sensor> sensorList;
    private final OnSensorClickListener listener;
    private int selectedPosition = -1;

    public SensorAdapter(List<Sensor> sensorList, OnSensorClickListener listener) {
        this.sensorList = sensorList;
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        int previous = selectedPosition;
        selectedPosition = position;
        if (previous >= 0 && previous < sensorList.size()) {
            notifyItemChanged(previous);
        }
        if (selectedPosition >= 0 && selectedPosition < sensorList.size()) {
            notifyItemChanged(selectedPosition);
        }
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        Sensor sensor = sensorList.get(position);

        holder.tvSensorName.setText(sensor.getName());
        holder.tvSensorType.setText(SensorUtils.getSensorTypeName(sensor.getType()));
        holder.tvSensorVendor.setText(String.format(Locale.US, "Vendor: %s", sensor.getVendor()));
        holder.tvSensorPower.setText(String.format(Locale.US, "Power: %.2f mA", sensor.getPower()));

        boolean isSelected = (position == selectedPosition);
        if (isSelected) {
            holder.cardSensorItem.setStrokeColor(holder.itemView.getContext().getColor(R.color.accent_purple));
            holder.cardSensorItem.setStrokeWidth(4);
        } else {
            holder.cardSensorItem.setStrokeColor(holder.itemView.getContext().getColor(R.color.card_stroke));
            holder.cardSensorItem.setStrokeWidth(2);
        }

        holder.itemView.setOnClickListener(v -> {
            int adapterPos = holder.getBindingAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION && listener != null) {
                setSelectedPosition(adapterPos);
                listener.onSensorClick(sensor, adapterPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sensorList != null ? sensorList.size() : 0;
    }

    public static class SensorViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView cardSensorItem;
        final TextView tvSensorName;
        final TextView tvSensorType;
        final TextView tvSensorVendor;
        final TextView tvSensorPower;

        SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSensorItem = itemView.findViewById(R.id.cardSensorItem);
            tvSensorName = itemView.findViewById(R.id.tvSensorName);
            tvSensorType = itemView.findViewById(R.id.tvSensorType);
            tvSensorVendor = itemView.findViewById(R.id.tvSensorVendor);
            tvSensorPower = itemView.findViewById(R.id.tvSensorPower);
        }
    }
}