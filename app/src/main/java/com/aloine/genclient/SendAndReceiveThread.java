package com.aloine.genclient;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.aloine.genclient.Constants.MESSAGE_READ;

/**
 * Sending and receiving of data happens on a thread.
 * To do this, you will need a bluetooth socket. Bluetoothsocket is responsible for for managing but incoming an outgoing connection. On the client side, it is used
 * manage outgoing connection
 */
public class SendAndReceiveThread extends Thread {

    private final BluetoothSocket mBluetoothSocket;
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;
    private Handler mHandler;


    public SendAndReceiveThread (BluetoothSocket socket, Handler mHandler) {
        mBluetoothSocket = socket;
        this.mHandler = mHandler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mInputStream = tmpIn;
        mOutputStream = tmpOut;

    }

    public void run() {
        byte[] bufferStorage = new byte[1024];
        int readBytes;

        while (true) {
            try {
                readBytes = mInputStream.available();
                if (readBytes != 0) {
                    SystemClock.sleep(50);
                    readBytes = mInputStream.available();
                    readBytes = mInputStream.read(bufferStorage,0,readBytes);
                    mHandler.obtainMessage(MESSAGE_READ,readBytes,-1,bufferStorage ).sendToTarget();

                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        }
    }
        public void writeToDevice(String input) {
        byte[] dataToSend = input.getBytes();

            try {
                mOutputStream.write(dataToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
