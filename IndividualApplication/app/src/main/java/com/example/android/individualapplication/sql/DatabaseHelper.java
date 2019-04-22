package com.example.android.individualapplication.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "EmployeeManager.db";

    private static final String TABLE_EMPLOYEE = "employee";

    private static final String COLUMN_EMPLOYEE_ID = "employee_id";
    private static final String COLUMN_EMPLOYEE_NAME = "employee_name";
    private static final String COLUMN_EMPLOYEE_EMAIL = "employee_email";
    private static final String COLUMN_EMPLOYEE_PASSWORD = "employee_password";

    private String CREATE_EMPLOYEE_TABLE = "CREATE TABLE " + TABLE_EMPLOYEE + "("
            + COLUMN_EMPLOYEE_ID + " INTEGER PRIMARY KEY," + COLUMN_EMPLOYEE_NAME + " TEXT,"
            + COLUMN_EMPLOYEE_EMAIL + " TEXT," + COLUMN_EMPLOYEE_PASSWORD + " TEXT" + ")";

    private String DROP_EMPLOYEE_TABLE = "DROP TABLE IF EXISTS " + TABLE_EMPLOYEE;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EMPLOYEE_TABLE);
        db.execSQL("INSERT INTO employee values (83488348,'Employee1', 'test.employee180@gmail.com', 'Welcome123@')");
        db.execSQL("INSERT INTO employee values (50582735,'Employee2', 'test.employee181@gmail.com', 'Welcome123@')");
        db.execSQL("INSERT INTO employee values (43851407,'Manager', 'test.manager180@gmail.com', 'Welcome123@')");
        db.execSQL("INSERT INTO employee values (42372153,'Team Leader', 'test.teamleader180@gmail.com', 'Welcome123@')");
        db.execSQL("INSERT INTO employee values (58807828,'Product Owner', 'test.productowner180@gmail.com', 'Welcome123@')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_EMPLOYEE_TABLE);
        onCreate(db);

    }

    public Cursor getRecord(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projections = {COLUMN_EMPLOYEE_ID, COLUMN_EMPLOYEE_NAME, COLUMN_EMPLOYEE_EMAIL};
        String selection = COLUMN_EMPLOYEE_ID +" LIKE ?";
        String employeeId= id.toString();
        String[] seletion_args={employeeId};
        Cursor cursor=db.query(TABLE_EMPLOYEE,projections,selection,seletion_args,null,null,null);
        return cursor;

    }
    public boolean checkAdmin(Integer id) {
        Log.d("IDCOMING",""+id);
        if(id == 43851407 || id==42372153 || id==58807828) {
            return true;
        }
        return false;
    }

    public boolean checkUser(Integer id, String password) {
        Log.d("id",""+id);
        Log.d("Password",password);
        String ID =id.toString();
        String[] columns = {
                COLUMN_EMPLOYEE_ID
        };
        Log.d("colums",columns[0]);
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_EMPLOYEE_ID + " = ?" + " AND " + COLUMN_EMPLOYEE_PASSWORD + " = ?";
        Log.d("Query",selection);
        String[] selectionArgs = {ID,password};
        Cursor cursor = db.query(TABLE_EMPLOYEE,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }
}
