package com.example.service;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final MutableLiveData<String> text = new MutableLiveData<>();

    private TextView textView;

    public static void setText(String text) {
        MainActivity.text.postValue(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);

        final Observer<String> textObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String text) {
                textView.setText(text);
            }
        };
        text.observe(this, textObserver);

        if (text.getValue() == null || text.getValue().isEmpty()) {
            text.setValue("0 calls");
        }
    }
}