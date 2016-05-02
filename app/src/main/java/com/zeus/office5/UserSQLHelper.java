package com.zeus.office5;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zeus on 4/28/2016.
 */
public class UserSQLHelper extends SQLiteOpenHelper {
    final static String DATABASE_NAME = "file_manager";
    final static String FAV_TABLE_NAME = "favourites";
    final static String FAV_TABLE_FILE_PATH = "file_path";
    final static String FAV_TABLE_DATE_ADDED = "date_added";
    final static String _ID = "_id";

    public UserSQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
