package com.example.currencyexchange;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
public class MainActivity extends AppCompatActivity {
    private String amount;
    private String currency;
    private double convertAmount =0;
    String URL = "https://api.exchangeratesapi.io/latest?base=USD";
    RequestQueue requestQueue;
    double pound;
    double euro;
    double rupee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest object = new JsonObjectRequest(Request.Method.GET, URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = response.getJSONObject("rates");
                            String gbp = obj.getString("GBP");
                            pound = Double.parseDouble(gbp);
                            String eur = obj.getString("EUR");
                            euro = Double.parseDouble(eur);
                            String inr = obj.getString("INR");
                            rupee = Double.parseDouble(inr);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(object);
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
            convertAmount = dollar * pound;
        } else if (currency.equals("Euro")) {
            dollar = Integer.parseInt(amount);
            convertAmount = dollar * euro;
        } else {
            dollar = Integer.parseInt(amount);
            convertAmount = dollar * rupee;
        }
        String exchangeValue = String.format("%.2f", convertAmount);
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
