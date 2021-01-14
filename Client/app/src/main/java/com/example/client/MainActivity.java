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
import android.widget.Toast;

import com.example.service.IRemoteService;

import java.util.Locale;
import java.util.Random;

// https://developer.android.com/guide/components/aidl
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AIDL-Client";
    IRemoteService iRemoteService;
    private Button bindButton;
    private Button sendButton;
    private TextView resultView;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindButton = findViewById(R.id.bind);
        bindButton.setText(iRemoteService == null ? R.string.bind_aidl_service : R.string.unbind_aidl_service);
        sendButton = findViewById(R.id.send);
        sendButton.setEnabled(iRemoteService != null);
        resultView = findViewById(R.id.result);
        resultView.setText(result);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "Service connected");
            Toast.makeText(MainActivity.this, R.string.service_connected, Toast.LENGTH_SHORT).show();
            iRemoteService = IRemoteService.Stub.asInterface(service);

            if (sendButton != null) bindButton.setText(R.string.unbind_aidl_service);
            if (sendButton != null) sendButton.setEnabled(true);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "Service has unexpectedly disconnected");
            Toast.makeText(MainActivity.this, R.string.service_disconnected, Toast.LENGTH_SHORT).show();
            iRemoteService = null;

            if (sendButton != null) bindButton.setText(R.string.bind_aidl_service);
            if (sendButton != null) sendButton.setEnabled(false);
        }
    };

    public void bindClick(View view) {
        if (iRemoteService == null) {
            // Bind to the AIDL service
            Intent intent = new Intent("com.example.service.RemoteService");
            intent.setPackage("com.example.service");
            boolean result = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Log.i(TAG, "bindService: " + result);
            if (!result) {
                Toast.makeText(MainActivity.this, R.string.service_binding_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Unbind from the AIDL service
            unbindService(mConnection);
            Log.e(TAG, "Service unbound");
            Toast.makeText(MainActivity.this, R.string.service_bound, Toast.LENGTH_SHORT).show();
            iRemoteService = null;

            if (sendButton != null) bindButton.setText(R.string.bind_aidl_service);
            if (sendButton != null) sendButton.setEnabled(false);
        }
    }

    public void sendClick(View view) {
        if (iRemoteService != null) {
            // Call the sum() method with two random integers
            Random random = new Random();
            int first = random.nextInt(10);
            int second = random.nextInt(10);
            try {
                int sum = iRemoteService.sum(first, second);
                Log.i(TAG, String.format(Locale.US, "sendClick() AIDL call of .sum(%d, %d) returned %d ", first, second, sum));
                result = String.valueOf(sum);
                if (resultView != null) resultView.setText(result);
            } catch (RemoteException e) {
                Log.e(TAG, "sendClick() RemoteException");
                Toast.makeText(MainActivity.this, R.string.remote_exception_occurred, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "sendClick() iRemoteService is null");
        }
    }
}