package com.example.android.individualapplication.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.android.individualapplication.R;
import com.example.android.individualapplication.sql.DatabaseHelper;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AdminActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper = new DatabaseHelper(AdminActivity.this);
    AppCompatTextView headingTextView;
    private String employeeId = "", employeeName = "", employeeEmail = "";
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    ProgressDialog mProgress;
    Date date = new Date();
    Button sheetUpdateButton;
    static final int REQUEST_ACCOUNT_PICKER = 100;
    static final int REQUEST_AUTHORIZATION = 400;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 200;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};
    AlertDialog.Builder adb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mOutputText = (TextView) findViewById(R.id.textView4);
        headingTextView = (AppCompatTextView) findViewById(R.id.headingTextView);
        Intent adminIntent = getIntent();
        String idString = adminIntent.getStringExtra("id");
        Integer id = Integer.parseInt(idString);
        retrieveFromSQLite(id);
        sheetUpdateButton = (Button) findViewById(R.id.button5);
        adb = new AlertDialog.Builder(AdminActivity.this);
        sheetUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSheet();
            }
        });
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Collecting data from Google Sheet ...");
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    public void retrieveFromSQLite(int id) {
        Cursor cursor = databaseHelper.getRecord(id);
        if (cursor.moveToFirst()) {
            employeeId = cursor.getString(0);
            employeeName = cursor.getString(1);
            employeeEmail = cursor.getString(2);
            headingTextView.setText("Welcome " + employeeName + "\n" + employeeEmail + "\n" + employeeId);
        }
    }

    private void getSheet() {
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            new AdminActivity.MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getSheet();
            } else {
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getSheet();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getSheet();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);

    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API")
                    .build();
        }

        protected List<String> doInBackground(Void... params) {
            try {
                return appendApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        public List<String> appendApi() throws IOException {
            String spreadsheetId = "1BdihNu66ns96S7Wo09SaKiYKJXOaWGV7OOlI7QTUruE";
            String range = "A2:E";
            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                results.add("Total Number of breaks: "+values.size());
                results.add("");
                for (List row : values) {
                    results.add(row.get(1) + "(" + row.get(0)+") - "+row.get(3));
                }
            }

            return results;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "");
                mOutputText.setText(TextUtils.join("\n", output));
                mOutputText.setMovementMethod(new ScrollingMovementMethod());
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {

                if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            DashboardActivity.REQUEST_AUTHORIZATION);
                } else {
                    adb.setTitle("Error");
                    adb.setMessage("The following error occurred:\n"
                            + mLastError.getMessage());
                    adb.setPositiveButton("Ok", null);
                    adb.show();
                }
            } else {
                adb.setTitle("Error");
                adb.setMessage("Request cancelled.\n"
                        + mLastError.getMessage());
                adb.setPositiveButton("Ok", null);
                adb.show();
            }
        }
    }
}
