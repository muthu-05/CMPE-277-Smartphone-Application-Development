package com.example.android.assignment2_implicitintent;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Launch(View v) {
        TextView urlTextView = (TextView) findViewById(R.id.urlTextView);
        String url = urlTextView.getText().toString();
        //Incase of empty text box there is no action on click of the button
        if(!url.equals("")) {
            Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + url));
            startActivity(urlIntent);
        }
    }

    public void Ring(View v) {
        TextView phoneTextView = (TextView) findViewById(R.id.phoneTextView);
        String phone = phoneTextView.getText().toString();
        //Incase of empty text box
        if(!phone.equals("")) {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(phoneIntent);
        }
    }

    public void CloseApp(View v) {
        finish();
    }
}
