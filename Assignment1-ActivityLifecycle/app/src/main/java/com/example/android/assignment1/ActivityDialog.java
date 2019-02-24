package com.example.android.assignment1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import static com.example.android.assignment1.ActivityA.restartCounter;

public class ActivityDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        restartCounter = restartCounter + 1;
    }
    public void finishDialog(View v) {
        ActivityDialog.this.finish();
    }

}
