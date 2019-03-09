package com.example.currencyconverter;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] currencySpinner = new String[]{"British Pound", "Euro", "Indian Rupee"};
        spinner = (Spinner) findViewById(R.id.currencySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();

        if (null != intent) {
            String amount = intent.getStringExtra("amount");
            String currency = intent.getStringExtra("currency");
            String exchangeValue = intent.getStringExtra("exchangeValue");

            if (null != exchangeValue) {
                TextView displayTextView = findViewById(R.id.displayTextView);
                displayTextView.setText("Dollar Amount $" + amount + " converted to " + exchangeValue + " " + currency);
            }
        }
    }
    public void convert(View view) {

        EditText amountEditText = (EditText) findViewById(R.id.amountEditText);
        String amount = amountEditText.getText().toString();
        if(!amount.equals("")) {
            String currency = spinner.getSelectedItem().toString();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setAction("com.example.currencyconverter.broadcast");
            intent.putExtra("amount", amount);
            intent.putExtra("currency", currency);
            intent.setComponent(new ComponentName("com.example.currencyexchange", "com.example.currencyexchange.CurrencyExchange"));
            sendBroadcast(intent);
        }

    }
    public void closeApp(View view) {
        MainActivity.this.finish();
    }
}
