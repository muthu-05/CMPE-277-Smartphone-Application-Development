package com.example.currencyconverter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CurrencyConverter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String exchangeValue = intent.getStringExtra("exchangeValue");
        String amount = intent.getStringExtra("amount");
        String currency = intent.getStringExtra("currency");

        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.putExtra("exchangeValue", exchangeValue);
        activityIntent.putExtra("amount", amount);
        activityIntent.putExtra("currency", currency);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }
}
