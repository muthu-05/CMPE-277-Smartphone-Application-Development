package com.example.android.assignment1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static com.example.android.assignment1.ActivityA.restartCounter;

public class ActivityB extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        restartCounter = restartCounter + 1;
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    public void finishActivityB(View view) {
        ActivityB.this.finish();
    }

}
