package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.service.IRemoteService;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Client";
    IRemoteService iRemoteService;
    private Button sendButton;
    private TextView resultView;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendButton = findViewById(R.id.send);
        sendButton.setEnabled(iRemoteService != null);
        resultView = findViewById(R.id.result);
        resultView.setText(result);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            Log.i(TAG, "Service connected");
            iRemoteService = IRemoteService.Stub.asInterface(service);
            if (sendButton != null) sendButton.setEnabled(true);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "Service has unexpectedly disconnected");
            iRemoteService = null;
            if (sendButton != null) sendButton.setEnabled(false);
        }
    };

    public void bindClick(View view) {
        Intent intent = new Intent("com.example.service.RemoteService");
        intent.setPackage("com.example.service");
//        Intent intent = new Intent(this, RemoteService.class);
//        intent.setAction(IRemoteService.class.getName());
        boolean result = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "bind: " + result);
    }

    public void sendClick(View view) {
        if (iRemoteService != null) {
            Random random = new Random();
            try {
                int sum = iRemoteService.sum(random.nextInt(10), random.nextInt(10));
                Log.i(TAG, "sendClick() sum: " + sum);
                result = String.valueOf(sum);
                if (resultView != null) resultView.setText(result);
            } catch (RemoteException | IllegalStateException e) {
                Log.e(TAG, "sendClick() RemoteException");
                e.printStackTrace();
            }
        } else Log.e(TAG, "sendClick() iRemoteService is null");
    }
}