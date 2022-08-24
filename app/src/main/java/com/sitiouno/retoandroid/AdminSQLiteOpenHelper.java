package com.sitiouno.retoandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "local cards";

    private static final String TABLE_NAME = "ADMINISTRATOR";
    private static final String COL_0 = "_ID";
    private static final String COL_1 = "FULLNAME";
    private static final String COL_2 = "EMAIL";
    private static final String COL_3 = "CODE";

    //constructor
    public AdminSQLiteOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //Create database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_NAME + 
                        "(" 
                        + COL_0 + " String PRIMARY KEY, "
                        + COL_1 + " String UNIQUE NOT NULL, "
                        + COL_2 + " String UNIQUE NOT NULL, "
                        + COL_3 + " string UNIQUE NOT NULL" +
                        ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate((sqLiteDatabase));
    }

    public boolean registerUser(@NonNull String _id,
                                @NonNull String fullname,
                                @NonNull String email,
                                @NonNull String code
    ) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        try {
            values.put(COL_0, _id);
            values.put(COL_1, fullname);
            values.put(COL_2, email);
            values.put(COL_3, code);

            long result = db.insert(TABLE_NAME, null, values);

            closeDatabase(db);
            return result != -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Check is user is already registered
    public Cursor consultUser() throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor user = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

            if (user.moveToFirst()) {
                closeDatabase(db);
                return user;
            } else {
                closeDatabase(db);
                return null;
            }
        } catch (SQLException e) {
            closeDatabase(db);
            e.printStackTrace();
        }
        closeDatabase(db);
        return null;
    }

    //UpdateUser
    public int updateUsers(
            @NonNull String _id,
            @NonNull String fullname,
            @NonNull String email,
            @NonNull String code

    ) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();

            values.put(COL_0, _id);
            values.put(COL_1, fullname);
            values.put(COL_2, email);
            values.put(COL_3, code);

            int user = db.update(TABLE_NAME, values, null, null);

            closeDatabase(db);
            return user;
        } catch (SQLException e) {
            closeDatabase(db);
            e.printStackTrace();
        }
        closeDatabase(db);
        return 0;
    }


    private void closeDatabase(SQLiteDatabase db) {
        try {
            //db.close();
            //Log.i("SQLite", "Closing database");
        } catch (
                SQLException e) {
            e.printStackTrace();
            Log.e("SQLite", "Error closing database");
        }


    }
}
