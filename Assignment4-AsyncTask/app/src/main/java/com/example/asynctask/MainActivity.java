package com.example.asynctask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements UIUpdate{

    EditText temperatureEditText;
    EditText humidityEditText;
    EditText activityEditText;
    EditText sensorEditText;
    TextView outputTextView;
    Button generateButton;
    THAsyncTask thasynctask;
    int sensorNumber;
    String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureEditText = (EditText) findViewById(R.id.temperatureEditText);
        humidityEditText = (EditText) findViewById(R.id.humidityEditText);
        activityEditText = (EditText) findViewById(R.id.activityEditText);
        generateButton = (Button) findViewById(R.id.generateButton);
        outputTextView = (TextView) findViewById(R.id.outputTextView);

        temperatureEditText.setFocusable(false);
        humidityEditText.setFocusable(false);
        activityEditText.setFocusable(false);
    }

    public void generate(View view) {
        sensorEditText = (EditText) findViewById(R.id.sensorEditText);
        input=sensorEditText.getText().toString();
        sensorNumber= Integer.parseInt(input);
        temperatureEditText.setText("");
        humidityEditText.setText("");
        activityEditText.setText("");
        thasynctask = new THAsyncTask(MainActivity.this);
        thasynctask.execute(sensorNumber);
    }

    public void cancel(View view) {
        thasynctask.cancel(true);
    }

    @Override
    public void UpdateUI(int temperature, int humidity, int activity, int sensorCount) {
        temperatureEditText.setText(temperature + " F");
        humidityEditText.setText(humidity + "%");
        activityEditText.setText(activity + "");
        sensorEditText.setText(sensorNumber-- +"");
        outputTextView.append("Output "+(sensorCount)+":\n"+"Temperature: "+temperature+" F\n" +"Humidity: "+humidity+"%\n"+"Activity: "+activity+"\n\n");
        outputTextView.setMovementMethod(new ScrollingMovementMethod());
    }
}
