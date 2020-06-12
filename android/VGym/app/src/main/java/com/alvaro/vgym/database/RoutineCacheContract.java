package com.alvaro.vgym.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class RoutineCacheContract
{
    private RoutineCacheContract() { }

    public static class RoutineCache implements BaseColumns
    {
        public static final String TABLE_NAME = "cache";
        public static final String COLUMN_NAME_ROUTINE = "routine";

        private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RoutineCache.TABLE_NAME + " (" +
            RoutineCache._ID + " INTEGER PRIMARY KEY," +
            RoutineCache.COLUMN_NAME_ROUTINE + " BLOB)";

        private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RoutineCache.TABLE_NAME;
    }

    public static class RoutineCacheDbHelper extends SQLiteOpenHelper
    {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "RoutineCache.db";

        public RoutineCacheDbHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) { db.execSQL(RoutineCache.SQL_CREATE_ENTRIES); }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL(RoutineCache.SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
