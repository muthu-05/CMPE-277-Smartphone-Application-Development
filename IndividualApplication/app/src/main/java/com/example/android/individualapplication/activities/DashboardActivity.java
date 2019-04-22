package com.example.android.individualapplication.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.individualapplication.R;
import com.example.android.individualapplication.sql.DatabaseHelper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class DashboardActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper = new DatabaseHelper(DashboardActivity.this);
    TextView headingTextView;
    Spinner spinner;
    private String employeeId, employeeName, employeeEmail;
    RadioGroup absenceRadioGroup;
    RadioButton absenceButton;
    String reasonOfAbsence;
    HashMap<String, String> receiverHashMap;
    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    Date date = new Date();
    Button sheetUpdateButton;
    String savedTime = "";
    SharedPreferences sharedPreferences;
    static final String PREFS_NAME = "SAVEDTIME";
    static final int REQUEST_ACCOUNT_PICKER = 100;
    static final int REQUEST_AUTHORIZATION = 400;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 202;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 200;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};
    AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        headingTextView = (TextView) findViewById(R.id.headingTextView);
        spinner = (Spinner) findViewById(R.id.receiverSpinner);
        absenceRadioGroup = (RadioGroup) findViewById(R.id.absenceRadioGroup);
        sheetUpdateButton = (Button) findViewById(R.id.sheetUpdateButton);
        Intent dashboardIntent = getIntent();
        String idString = dashboardIntent.getStringExtra("id");
        Integer id = Integer.parseInt(idString);
        retrieveFromSQLite(id);
        String[] receiverSpinner = new String[]{"Manager", "Team Leader", "Product Owner"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, receiverSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        receiverHashMap = new HashMap<String, String>();
        receiverHashMap.put("Manager", "test.manager180@gmail.com");
        receiverHashMap.put("Team Leader", "test.teamleader180@gmail.com");
        receiverHashMap.put("Product Owner", "test.productowner180@gmail.com");

        //Google Sheets
        adb = new AlertDialog.Builder(DashboardActivity.this);


        sheetUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = absenceRadioGroup.getCheckedRadioButtonId();
                absenceButton = (RadioButton) findViewById(selectedId);
                reasonOfAbsence = absenceButton.getText().toString();
                adb.setTitle("Confirmation")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getResultsFromApi();
                            }
                        });
                AlertDialog alert = adb.create();
                alert.show();
            }
        });
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Updating Google Sheet ...");
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    public void sendingEmail(View view) {
        String designation = spinner.getSelectedItem().toString();
        String[] mail = {receiverHashMap.get(spinner.getSelectedItem())};
        String emailSubject = "Personal Leave Application for a Day";
        int selectedId = absenceRadioGroup.getCheckedRadioButtonId();
        absenceButton = (RadioButton) findViewById(selectedId);
        reasonOfAbsence = absenceButton.getText().toString();
        String emailText = "Dear " + designation + "\n\n I am writing this mail to inform you that I need to take a day of absence on " + sdf.format(date) + " of this month due to " + reasonOfAbsence + ". You can contact me at my mail " + employeeEmail + " in case of questions or clarifications.\n\nSincerely,\n" + employeeName;
        String emailList[] = {"muthu220515@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, mail);
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, emailText);
        startActivity(Intent.createChooser(intent, "Choice email App"));
    }

    public void retrieveFromSQLite(int id) {
        Cursor cursor = databaseHelper.getRecord(id);
        if (cursor.moveToFirst()) {
            employeeId = cursor.getString(0);
            employeeName = cursor.getString(1);
            employeeEmail = cursor.getString(2);
            headingTextView.setText("Welcome " + employeeName+"\n"+employeeEmail+"\n"+employeeId);
        }
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            adb.setTitle("Network Problem");
            adb.setMessage("No network connection available.");
            adb.setPositiveButton("Ok", null);
            adb.show();
        } else {
            new MakeRequestTask(mCredential).execute();
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
                getResultsFromApi();
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
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    adb.setTitle("Error");
                    adb.setMessage("\"This app requires Google Play Services. Please install \" +\n" +
                            "                                    \"Google Play Services on your device and relaunch this app.\"");
                    adb.setPositiveButton("Ok", null);
                    adb.show();
                } else {
                    getResultsFromApi();
                }
                break;
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
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
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

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                DashboardActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
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
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        public List<String> getDataFromApi() throws IOException {
            String spreadsheetId = "1BdihNu66ns96S7Wo09SaKiYKJXOaWGV7OOlI7QTUruE";
            String range = "A2";
            String a1 = employeeId;
            String b1 = employeeName;

            String c1 = employeeEmail;
            Object d1 = ""+sdf.format(date);
            String e1 = reasonOfAbsence;
            ValueRange valueRange = new ValueRange();
            valueRange.setValues(
                    Arrays.asList(
                            Arrays.asList(a1, b1, c1, d1, e1)));
            Log.d("valueRange", "" + valueRange);
            List<String> results = new ArrayList<String>();
            AppendValuesResponse response = this.mService.spreadsheets().values().append(spreadsheetId, range, valueRange)
                    .setValueInputOption("RAW")
                    .execute();

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
                savedTime = DateFormat.format("MM/dd/yyyy", new Date((new Date()).getTime())).toString();
                sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("savedTime", savedTime);
                Log.d("savedTime",savedTime);
                editor.commit();
                Toast.makeText(getApplicationContext(), "Google Sheet Updated", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Google Sheet Unable to Update", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
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
