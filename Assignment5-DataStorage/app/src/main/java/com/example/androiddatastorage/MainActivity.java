package com.example.androiddatastorage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onResume() {
        super.onResume();
        try
        {
            InputStream inputStream=openFileInput(PreferenceView.STORE_PREFERENCES);
            if(inputStream!=null)
            {
                InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                String dataInfo;
                StringBuilder stringBuilder=new StringBuilder();
                while((dataInfo=bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(dataInfo +"\n");
                }
                inputStream.close();
                TextView storedFiles=(TextView)findViewById(R.id.storedFiles);
                storedFiles.setText(stringBuilder.toString());
                storedFiles.setMovementMethod(new ScrollingMovementMethod());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void preferenceView(View view)
    {
        Intent intent=new Intent(this,PreferenceView.class);
        startActivity(intent);
    }

    public void sqliteView(View view)
    {
        Intent intent=new Intent(this,SQLiteView.class);
        startActivity(intent);
    }

    public void close(View view)
    {
        MainActivity.this.finish();
    }
}
