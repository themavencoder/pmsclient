package com.aloine.genclient;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private List<DeviceInfo> mDeviceList = null;
    private OnDeviceClickListener mListener;


    public RecyclerAdapter(OnDeviceClickListener listener, List<DeviceInfo> deviceList) {
        this.mListener = listener;
        this.mDeviceList = deviceList;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_device, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DeviceInfo deviceInfo = mDeviceList.get(position);
        holder.bind(mListener,deviceInfo);

    }

    @Override
    public int getItemCount() {
        return null != mDeviceList ? mDeviceList.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mmTextDeviceName, mmTextMac;
        private CardView mmCardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mmTextDeviceName = itemView.findViewById(R.id.textview_device_name);
            mmTextMac = itemView.findViewById(R.id.textview_device_mac);
            mmCardView = itemView.findViewById(R.id.cardview_item);
        }

        public void bind(final OnDeviceClickListener listener, final DeviceInfo deviceInfo) {
            mmCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.setOnDeviceClickListener(deviceInfo);
                }
            });
            mmTextDeviceName.setText(deviceInfo.getNameOfDevice());
            mmTextMac.setText(deviceInfo.getMacAddress());

        }
    }
}
