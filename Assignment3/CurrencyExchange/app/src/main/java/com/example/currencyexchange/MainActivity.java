package com.example.currencyexchange;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private String amount;
    private String currency;
    private double convertAmount =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (null != intent) {
            received(intent);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (null != intent) {
            received(intent);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        if (null != intent) {
            received(intent);
        }
    }
    public void received(Intent intent) {

        amount = intent.getStringExtra("amount");
        TextView textView = findViewById(R.id.amountTextView);
        textView.setText("Dollar Amount : $ " + amount);

        currency = intent.getStringExtra("currency");
        TextView textView2 = findViewById(R.id.currencyTextView);
        textView2.setText("Convert To : " + currency);

    }
    public void convert(View view) {
        int dollar = 0;
        if (currency.equals("British Pound")) {
            dollar = Integer.parseInt(amount);
            convertAmount = dollar * 0.76;
        } else if (currency.equals("Euro")) {
            dollar = Integer.parseInt(amount);
            convertAmount = dollar * 0.88;
        } else {
            dollar = Integer.parseInt(amount);
            convertAmount = dollar * 70.58;
        }
        String exchangeValue = Double.toString(convertAmount);
        exchange(exchangeValue);
    }
    public void exchange(String exchangeValue){
        Intent intent = new Intent();
        intent.setAction("com.example.currencyexchange.broadcast");
        intent.addFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
        intent.setComponent(new ComponentName("com.example.currencyconverter", "com.example.currencyconverter.CurrencyConverter"));
        intent.putExtra("exchangeValue", exchangeValue);
        intent.putExtra("amount", amount);
        intent.putExtra("currency", currency);

        MainActivity.this.finish();
        sendBroadcast(intent);

    }

    public void closeApp(View view)
    {
        MainActivity.this.finish();
    }
}
