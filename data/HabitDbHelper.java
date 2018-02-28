package com.example.fengzi113.habit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fengzi113 on 2018/2/26.
 */

public  class HabitDbHelper extends SQLiteOpenHelper {

    private static final String DATEBASE_NAME = "shelter.db";
    private static final int DATEBASE_VERSION = 1;

    public HabitDbHelper(Context context) {
        super(context, DATEBASE_NAME, null, DATEBASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

     String SQL_CREATE_ENTRIES =   "CREATE TABLE " + HabitContract.HabitEntry.TABLE_NAME + "("+
             HabitContract.HabitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
             HabitContract.HabitEntry.COLUMN_HABIT_THING + " TEXT NOT NULL,"+
             HabitContract.HabitEntry.COLUMN_HABIT_DURATION + " TEXT NOT NULL" +");";
        db.execSQL(SQL_CREATE_ENTRIES);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
