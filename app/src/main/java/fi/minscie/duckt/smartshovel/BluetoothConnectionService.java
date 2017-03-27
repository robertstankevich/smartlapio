package fi.minscie.duckt.smartshovel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.app.Application;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by DuckT on 18/02/2017.
 */

public class BluetoothConnectionService extends Main2Activity{


    private static int top_temp;
    private static int status;
    private static float weight;

    public static int getTop_Temp() {
        return top_temp;
    }
    public static int getStatus() {
        return status;
    }
    public static float getWeight() { return weight; }



    private BluetoothSocket mSocket = null;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;

    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "HC-06";

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private AcceptThread mInsecureAcceptThread;

    private ConnectThread mConnectThread;
    private BluetoothDevice mDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;

    private ConnectedThread mConnectedThread;

    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }




    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

                Log.d(TAG, "Accept Thread: Setting up server using: " + MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "Accept Thread IOException: " + e.getMessage());
            }
            mServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread Running.");

            BluetoothSocket socket = null;

            try {
                Log.d(TAG, "run: RFCOM server socket start.... ");

                socket = mServerSocket.accept();

                Log.d(TAG, "run: RFCOM Server socket accepted connection.");

            } catch (IOException e) {
                Log.e(TAG, "Accept Thread IOException: " + e.getMessage());
            }
            if (socket != null){
                connected(socket, mDevice);
            }

            Log.i(TAG, "end mAcceptThread");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Cancel Accept Thread.");
            try {
                mServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }


        private class ConnectThread extends Thread {
            private BluetoothSocket mSocket;

            public ConnectThread(BluetoothDevice device, UUID uuid) {
                Log.d(TAG, "ConnectThread: started.");
                mDevice = device;
                deviceUUID = uuid;
            }

            public void run() {

                BluetoothSocket tmp = null;

                Log.i(TAG, "run: mConnectThread");

                try {
                    Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID " + MY_UUID_INSECURE);
                    tmp = mDevice.createRfcommSocketToServiceRecord(deviceUUID);
                } catch (IOException e) {
                    Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
                }

                mSocket = tmp;


                mBluetoothAdapter.cancelDiscovery();

                try {
                    mSocket.connect();
                    Log.d(TAG, "run: ConnectThread: Connected.");
                } catch (IOException e) {
                    try {
                        mSocket.close();
                        Log.d(TAG, "run: ConnectThread: Closed. " + e.getMessage());
                    } catch (IOException e1) {
                        Log.e(TAG, "run: ConnectThread: Unable to close connection. " + e.getMessage());
                    }
                    Log.d(TAG, "run: Could not connect the UUID " + MY_UUID_INSECURE);
                }

                connected(mSocket, mDevice);
            }

            public void cancel() {
                try {
                    Log.d(TAG, "cancel: Closing Client socket.");
                    mSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "cancel: close() of mSocket failed. " + e.getMessage());
                }
            }
        }



    public synchronized void start(){
            Log.d(TAG, "start");
            if(mConnectThread != null){
                mConnectThread.cancel();
                mConnectThread = null;
            }
            if(mInsecureAcceptThread == null){
                mInsecureAcceptThread = new AcceptThread();
                mInsecureAcceptThread.start();
            }

        }




    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: Started.");

        mProgressDialog = ProgressDialog.show(mContext, "Connecting Bluetooth", "Please Wait...", true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    public String writeMessage;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int)msg.arg1;
            int end = (int)msg.arg2;

            switch (msg.what){
                case 1:
                    writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    Log.d(TAG, "Reading inputstream: " + writeMessage);
                    if(writeMessage.substring(0,1).matches("T")){
                        try {
                            int value = Integer.parseInt(writeMessage.substring(writeMessage.lastIndexOf("T") + 1));
                            if (value < 55 && value > -55) {
                                top_temp = value;
                            }
                            Log.d(TAG, "Got top_temp value: " + top_temp);
                        }
                        catch(Exception e){
                            Log.e(TAG,"Failed to parse value! "+e);
                        }
                        }
                    if(writeMessage.substring(0,1).matches("S")) {
                        try {
                            int value = Integer.parseInt(writeMessage.substring(writeMessage.lastIndexOf("S") + 1));
                            if (value == 1 || value == 0) {
                                status = value;
                            }
                            if (value == 1) {
                                weight = 0;
                            }
                            Log.d(TAG, "Got status value: " + status);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to parse value! " + e);
                        }
                    }

                    if(writeMessage.substring(0,1).matches("W")){
                        try{
                        float value =  Float.parseFloat(writeMessage.substring(writeMessage.lastIndexOf("W")+1));
                        if(value > 0.15 && value < 50) {
                            weight = value;
                        }
                        Log.d(TAG, "Got weight value: " + weight);
                        }
                        catch(Exception e){
                            Log.e(TAG,"Failed to parse value! "+e);
                        }
                    }


                    break;
            }
        }
    };

    private class ConnectedThread extends Thread{


        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "ConnectedThread: Starting.");

            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            mProgressDialog.dismiss();


            try {
                tmpIn = mSocket.getInputStream();
                tmpOut = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mInStream = tmpIn;
            mOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes = 0;
            int begin = 0;

            while (true){
                try {

                    bytes += mInStream.read(buffer, bytes, buffer.length - bytes);
                } catch (IOException e) {
                    Log.e(TAG, "Error reading inputstream: " + e.getMessage());
                    break;
                }

                for(int i=begin; i< bytes; i++){
                    if(buffer[i] == "#".getBytes()[0]){
                        mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                        begin = i + 1;
                        if(i == bytes - 1){
                            bytes = 0;
                            begin = 0;
                        }

                    }
                }
            }

        }



        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Error writing outputstream: " + e.getMessage());
            }
        }


        public void cancel(){
            try {
                mSocket.close();
            } catch (IOException e){

            }
        }
    }
    private void connected(BluetoothSocket mSocket, BluetoothDevice mDevice) {
            Log.d(TAG, "connected: Starting.");

            mConnectedThread = new ConnectedThread(mSocket);
            mConnectedThread.start();
        }


    public void write(byte[] out){
        ConnectedThread r;

        Log.d(TAG, "write: Write Called.");
        mConnectedThread.write(out);
    }
}
