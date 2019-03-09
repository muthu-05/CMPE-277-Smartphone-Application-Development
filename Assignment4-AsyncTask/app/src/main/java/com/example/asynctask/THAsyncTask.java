package com.example.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Random;

public class THAsyncTask extends AsyncTask<Integer, Integer, Integer> {
    private ArrayAdapter<String> adpater;
    int sensor;
    UIUpdate uiupdate = null;

    public THAsyncTask(UIUpdate update) {
        uiupdate = update;
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        Random random = new Random();
        int tmin=25, tmax=100;
        int hmin=40, hmax=100;
        int amin=1, amax=500;
        for (sensor=1; sensor <= params[0];sensor++)
        {
            int temperature = random.nextInt((tmax - tmin) + 1) + tmin;
            int humidity = random.nextInt((hmax - hmin) + 1) + hmin;
            int activity = random.nextInt((amax - amin) + 1) + amin;
            publishProgress(temperature,humidity,activity,sensor);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isCancelled()) {
                break;
            }
        }
        return 1;
    }

    @Override
    protected void onPreExecute() {
        MainActivity mainActivity = (MainActivity) uiupdate;
        mainActivity.generateButton.setClickable(false);
        Toast.makeText(mainActivity, "Generating Outputs", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(Integer... params) {
        this.uiupdate.UpdateUI(params[0],params[1],params[2],params[3]);
        Toast.makeText((Context) uiupdate, "Output " + sensor, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Integer params) {
        MainActivity  mainActivity = (MainActivity) uiupdate;
        mainActivity.generateButton.setClickable(true);
        Toast.makeText(mainActivity, "Tasks Completed", Toast.LENGTH_SHORT).show();
    }
}
