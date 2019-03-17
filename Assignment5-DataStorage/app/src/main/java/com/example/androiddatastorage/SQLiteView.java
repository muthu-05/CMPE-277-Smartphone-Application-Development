package com.example.androiddatastorage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteView extends AppCompatActivity {
    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM/dd/yyyy-hh:mm a");
    public int counter=0;
    private int i=0;
    private String blogMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_view);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        counter=sharedPrefs.getInt("SQL_COUNTER", 0);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        counter=sharedPrefs.getInt("SQL_COUNTER", 0);
    }

    public void save(View view)
    {
        EditText blogEditText=(EditText)findViewById(R.id.blogEditText);
        String message=blogEditText.getText().toString();
        DataController dataController=new DataController(getBaseContext());
        dataController.open();
        long returnValue= dataController.insert(message);
        Cursor cursor = dataController.retrieve();
        if(cursor.moveToFirst()) {
            do {
                blogMessage = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        dataController.close();
        if(returnValue!=-1 && !message.equals(""))
        {
            try
            {
                counter+=1;
                SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("SQL_COUNTER", counter);
                editor.commit();
                OutputStreamWriter out=new OutputStreamWriter(openFileOutput(PreferenceView.STORE_PREFERENCES,MODE_APPEND));
                out.write("\nSQLite "+counter+"\nBlogMessage:"+blogMessage+"\n"+simpleDateFormat.format(new Date()));
                out.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Invalid Entry", Toast.LENGTH_LONG).show();
        }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
    }

    public void cancel(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        SQLiteView.this.finish();
    }

}