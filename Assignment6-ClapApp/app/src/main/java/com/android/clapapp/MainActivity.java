package com.android.clapapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorProximity;
    private MediaPlayer mediaPlayer;
    private TextView displayTextView;
    private final static int MAX_VOLUME = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayTextView = (TextView) findViewById(R.id.displayTextView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mediaPlayer = MediaPlayer.create(this, R.raw.audio);
    }

    @Override
    protected void onResume()   {
        super.onResume();
        sensorManager.registerListener(this, sensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mediaPlayer.start();
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (event.values[0] == 0) {
            audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            displayTextView.setText("Phone is Near");

        } else {
            audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            displayTextView.setText("Phone is Far");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public void closeApp(View v) {
        MainActivity.this.finish();
    }

}
