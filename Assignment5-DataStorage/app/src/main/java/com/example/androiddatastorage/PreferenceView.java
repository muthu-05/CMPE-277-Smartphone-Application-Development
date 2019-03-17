package com.example.androiddatastorage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PreferenceView extends AppCompatActivity {

    EditText nameEditText;
    EditText authorEditText;
    EditText descriptionEditText;
    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM/dd/yyyy-hh:mm a");
    public int counter=0;
    public final static String STORE_PREFERENCES="preferences.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_view);

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        counter=sharedpreferences.getInt("COUNTER", 0);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        counter=sharedPrefs.getInt("COUNTER", 0);
    }


    public void save(View view)
    {
        nameEditText = (EditText)findViewById(R.id.nameEditText);
        String name = nameEditText.getText().toString();
        authorEditText = (EditText)findViewById(R.id.authorEditText);
        String author = authorEditText.getText().toString();
        descriptionEditText = (EditText)findViewById(R.id.descriptionEditText);
        String description = descriptionEditText.getText().toString();

        if(!name.equals("") && !author.equals("") && !description.equals(""))
        {
            try{
                counter+=1;

                SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt("COUNTER", counter);
                editor.putString("BookName", name);
                editor.putString("BookAuthor", author);
                editor.putString("Description",description);
                editor.commit();

                String bookName = sharedPreferences.getString("BookName", "null");
                String bookAuthor = sharedPreferences.getString("BookAuthor", "null");
                String bookDescription = sharedPreferences.getString("Description", "null");
                OutputStreamWriter outputStreamWriter=new OutputStreamWriter(openFileOutput(STORE_PREFERENCES,MODE_APPEND));
                String message = "\nSaved Preference " +counter+ "\nBookName: " + bookName + "\nBookAuthor: " + bookAuthor + "\nDescription: " + bookDescription+"\n" + simpleDateFormat.format(new Date());
                outputStreamWriter.write(message);
                outputStreamWriter.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
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
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        PreferenceView.this.finish();
    }
}
