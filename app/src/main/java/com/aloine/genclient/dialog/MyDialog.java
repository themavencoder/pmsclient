package com.aloine.genclient.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.aloine.genclient.DeviceInfo;
import com.aloine.genclient.MainActivity;
import com.aloine.genclient.OnDeviceClickListener;
import com.aloine.genclient.Persistor;
import com.aloine.genclient.ProgressCallBack;
import com.aloine.genclient.R;
import com.aloine.genclient.RecyclerAdapter;
import com.aloine.genclient.SendAndReceiveThread;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.aloine.genclient.Constants.CHECK_DIALOG_STATE;
import static com.aloine.genclient.Constants.CONNECTING_STATUS;

public class MyDialog extends DialogFragment implements OnDeviceClickListener {
    private View v;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private List<DeviceInfo> deviceList;
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket mBluetoothSocket = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Handler mHandler;
    private SendAndReceiveThread sendAndReceiveThread;
    private ProgressCallBack progressCallBack;

    public MyDialog() {

    }

    public void setProgressCallBack(ProgressCallBack progressCallBack) {
        this.progressCallBack = progressCallBack;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;

    }
    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.mBluetoothSocket = bluetoothSocket;

    }
    public SendAndReceiveThread getSendAndReceiveData() {
        return sendAndReceiveThread;

    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.custom_dialog_layout, null);
        builder.setView(v);


        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerAdapter = new RecyclerAdapter(this, MainActivity.deviceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(recyclerAdapter);

        return builder.create();

    }


    @Override
    public void setOnDeviceClickListener(DeviceInfo deviceInfo) {
        Persistor persistor = Persistor.getInstance();
        final String name = deviceInfo.getNameOfDevice();
        final String address = deviceInfo.getMacAddress();
         getDialog().hide();
        progressCallBack.showProgressBar();

        final Thread thread = new Thread() {
            @Override
            public void run() {

                boolean fail = false;
                BluetoothDevice device = adapter.getRemoteDevice(address);
                try {
                    mBluetoothSocket = createBluetoothSocket(device);
                } catch (IOException e) {
                    progressCallBack.hideProgressBar();
                    fail = true;
                    Toast.makeText(getActivity(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                }
                try {
                    mBluetoothSocket.connect();
                } catch (IOException ex) {
                    try {
                        progressCallBack.hideProgressBar();
                        fail = true;
                        mBluetoothSocket.close();
                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name).sendToTarget();

                    } catch (IOException ee) {

                    }
                }
                if (fail == false) {
                    progressCallBack.hideProgressBar();
                    sendAndReceiveThread = new SendAndReceiveThread(mBluetoothSocket, mHandler);
                    sendAndReceiveThread.start();

                    mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                            .sendToTarget();
                }

            }
        };

        thread.start();


    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
}
