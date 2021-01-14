package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteService extends Service {

    private final AtomicInteger counter = new AtomicInteger(1);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IRemoteService.Stub binder = new IRemoteService.Stub() {
        @Override
        public int sum(int first, int second) throws RemoteException {
            int sum = first + second;
            MainActivity.setText(String.format(Locale.US, "%d calls, %d + %d = %d",
                    counter.getAndIncrement(), first, second, sum));
            return sum;
        }
    };
}
