package com.example.currencyexchange;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CurrencyExchange extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        String amount = intent.getStringExtra("amount");
        String currency = intent.getStringExtra("currency");

        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.putExtra("amount", amount);
        activityIntent.putExtra("currency", currency);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }

}