package com.example.android.assignment2_implicitintent;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Launch(View v) {
        EditText urlTextView = (EditText) findViewById(R.id.urlTextView);
        Uri url = Uri.parse("http://" + urlTextView.getText().toString());

        Intent urlIntent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(urlIntent);
        finish();

    }

    public void Ring(View v) {
        EditText phoneTextView = (EditText) findViewById(R.id.phoneTextView);
        Uri phone = Uri.parse("tel:" + phoneTextView.getText().toString());
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL,phone);
        startActivity(phoneIntent);

    }

    public void CloseApp(View v) {
        finish();
    }
}
