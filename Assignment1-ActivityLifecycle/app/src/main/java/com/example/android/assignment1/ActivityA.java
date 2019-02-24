package com.example.android.assignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivityA extends AppCompatActivity {
    public static int restartCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        restartCounter = restartCounter+1;
        TextView restartCounterView = (TextView)findViewById(R.id.restartCounterView);
        String display = String.format("%03d",restartCounter);
        restartCounterView.setText("Restart Counter: "+display);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        restartCounter = 0;
    }

    public void startActivityB(View view){
        startActivity(new Intent(ActivityA.this, ActivityB.class));
    }

    public void  viewDialog(View view){
        startActivity(new Intent(ActivityA.this, ActivityDialog.class));
    }

    public void finishActivityA(View view) {
        ActivityA.this.finish();
    }
}


