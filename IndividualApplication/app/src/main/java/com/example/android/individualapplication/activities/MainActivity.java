package com.example.android.individualapplication.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.android.individualapplication.R;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.example.android.individualapplication.helper.InputValidation;
import com.example.android.individualapplication.sql.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private EditText employeeIdEditText;
    private EditText employeePasswordEditText;
    private TextInputLayout textInputLayoutId;
    private TextInputLayout textInputLayoutPassword;
    private int id;
    private String password;
    ProgressDialog mProgress;
    private InputValidation inputValidation = new InputValidation(MainActivity.this);
    private DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
    private String TAG = "AccountsActivityTAG";
    private String wantPermission = Manifest.permission.GET_ACCOUNTS;
    private Activity activity = MainActivity.this;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    String accountName ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        employeeIdEditText = (EditText) findViewById(R.id.employeeIdEditText);
        employeePasswordEditText = (EditText) findViewById(R.id.employeePasswordEditText);
        textInputLayoutId = (TextInputLayout) findViewById(R.id.employeeIdTextInputLayout);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.employeePasswordTextInputLayout);
        if (!checkPermission(wantPermission)) {
            requestPermission(wantPermission);
        }
    }

    private boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void requestPermission(String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            Toast.makeText(activity, "Get account permission allows us to get your email",
                    Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    Toast.makeText(activity,"Permission Denied.",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void login( View view) {
        String idString = employeeIdEditText.getText().toString();
        if(idString.length()==0) {
            id=0;
        } else {
            id = Integer.parseInt(idString);
        }
        password = employeePasswordEditText.getText().toString().trim();
        Log.d("id",""+id);
        Log.d("password coming",password);

        if (!inputValidation.isInputIdFilled(id, textInputLayoutId,"Invalid ID")) {
            return;
        }
        if (!inputValidation.isInputTextFilled(employeePasswordEditText, textInputLayoutPassword, "Invalid Password")) {
            return;
        }

        if (databaseHelper.checkUser(id,password)) {
            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
            Intent adminIntent = new Intent(MainActivity.this, AdminActivity.class);
            adminIntent.putExtra("id", employeeIdEditText.getText().toString().trim());
            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
            Intent mainIntent = new Intent(MainActivity.this, DashboardActivity.class);
            mainIntent.putExtra("id", employeeIdEditText.getText().toString().trim());
            emptyInputEditText();
            if (databaseHelper.checkAdmin(id)) {
                startActivity(adminIntent);
            } else {
                startActivity(mainIntent);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
        }
    }

    public void emptyInputEditText() {
        employeeIdEditText.setText(null);
        employeePasswordEditText.setText(null);
    }
}
