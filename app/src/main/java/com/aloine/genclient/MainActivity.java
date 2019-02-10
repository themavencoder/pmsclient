package com.aloine.genclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aloine.genclient.dialog.MyDialog;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.aloine.genclient.Constants.CHECK_DIALOG_STATE;
import static com.aloine.genclient.Constants.CONNECTING_STATUS;
import static com.aloine.genclient.Constants.MESSAGE_READ;
import static com.aloine.genclient.Constants.REQUEST_BLUETOOTH_ENABLE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ProgressCallBack {
    private BluetoothAdapter mBluetoothAdapter;
    private CoordinatorLayout mCoordinatorLayout;
    private Button mButtonEstablishConnection, mButtonCheckGauge;
    private SendAndReceiveThread sendAndReceiveThread;
    private TextView mTextGauge;
    private Set<BluetoothDevice> mPairedDevice;
    private Persistor persistor;
    private Handler mHandler;
    private BluetoothSocket mBluetoothSocket = null;
    public static List<DeviceInfo> deviceList = new ArrayList<>();
    MyDialog myDialog;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        persistor = Persistor.getInstance();
        init();
        activateBluetooth();
        myDialog = new MyDialog();
        mHandler = getmHandler();
        myDialog.setProgressCallBack(this);
        myDialog.setHandler(mHandler);

       sendAndReceiveThread =  myDialog.getSendAndReceiveData();
    }

    @NonNull
    private Handler getmHandler() {
        return new Handler(){
            public void handleMessage(android.os.Message msg){
               // progressBar.setVisibility(View.VISIBLE);
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mTextGauge.setText("");
                    mTextGauge.setText(readMessage);
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1) {
                        //progressBar.setVisibility(View.GONE);
                        mButtonEstablishConnection.setEnabled(false);
                        mButtonEstablishConnection.setBackgroundColor(getResources().getColor(R.color.grey));
                        Toast.makeText(MainActivity.this, "Connected to Device", Toast.LENGTH_SHORT).show();
                    }
                    else
                       // progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                            myDialog.dismiss();

                       // mBluetoothStatus.setText("Connection Failed");
                }
            }
        };
    }

    private void init() {
        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        mTextGauge = findViewById(R.id.textview_read_gauge);
        mButtonCheckGauge = findViewById(R.id.button_check_gauge);
        mButtonEstablishConnection = findViewById(R.id.button_connect_paired_device);
        mButtonEstablishConnection.setOnClickListener(this);
        mButtonCheckGauge.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
    }
    private void activateBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothIntent,REQUEST_BLUETOOTH_ENABLE);
        } else {

            Snackbar snackbar  = displaySuccess("Device bluetooth activated");
            snackbar.show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == REQUEST_BLUETOOTH_ENABLE && resultCode == RESULT_OK) {
                Snackbar snackbar  = displaySuccess("Device bluetooth activated");
                snackbar.show();
      } else {
          finish();
      }

    }
    @NonNull
    private Snackbar displaySuccess(String s) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, s, Snackbar.LENGTH_INDEFINITE);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.white));
        return snackbar;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_connect_paired_device:
                if (!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(this, "Bluetooth not ON!", Toast.LENGTH_SHORT).show();
                    return;
                }
                deviceList.clear();
                listDevice();

                myDialog.show(getSupportFragmentManager(),"my_dialog");
                if (myDialog.isHidden()) {
                   /* Snackbar snackbar = displaySuccess("Connecting...");
                    snackbar.show();*/
                }
                break;
            case R.id.button_check_gauge:
                sendAndReceiveThread =  myDialog.getSendAndReceiveData();
                    if (sendAndReceiveThread != null) {
                        sendAndReceiveThread.writeToDevice("2");
                    }
                    else {
                        Toast.makeText(this, "Unable to get text from device", Toast.LENGTH_SHORT).show();
                    }
                    break;
            default:
        }


    }

    void listDevice() {
        mPairedDevice = mBluetoothAdapter.getBondedDevices();
        if (mBluetoothAdapter.isEnabled()) {
            for (BluetoothDevice device : mPairedDevice) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setNameOfDevice(device.getName());
                deviceInfo.setMacAddress(device.getAddress());
                deviceList.add(deviceInfo);
            }
        } else {
           //TODO
        }

    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        mButtonEstablishConnection.setEnabled(false);
    }


    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        mButtonEstablishConnection.setEnabled(true);

    }

}
