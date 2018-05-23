package com.example.edu.delivery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class basedatos extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "FeedReader.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NOMBRE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_APELLIDO + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_CELULAR + TEXT_TYPE + ")";

    private static final String SQL_CREATE_POSITION =
            "CREATE TABLE " + posicion.TABLE_NAME +
                    "(" + posicion._ID + " INTEGER PRIMARY KEY," +
                    posicion.COLUMN_LUGAR + TEXT_TYPE+COMMA_SEP + posicion.COLUMN_LUGAR_LATITUD+
                    TEXT_TYPE + COMMA_SEP + posicion.COLUMN_lUGAR_LONGITUD+TEXT_TYPE+")";

    private static final String SQL_CREATE_TERM =
            "CREATE TABLE " + info.TABLE_NAME +
                    "(" + info._ID + " INTEGER PRIMARY KEY," +
                    info.COLUMN_CHECK + TEXT_TYPE+")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    private static final String SQL_DELETE_POSITION =
            "DROP TABLE IF EXISTS " + posicion.TABLE_NAME;

    private static final String SQL_DELETE_INFO =
            "DROP TABLE IF EXISTS " + info.TABLE_NAME;

    public basedatos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_POSITION);
        db.execSQL(SQL_CREATE_TERM);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_POSITION);
        db.execSQL(SQL_DELETE_INFO);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_APELLIDO = "apellido";
        public static final String COLUMN_CELULAR = "celular";


    }
    public  static class posicion implements  BaseColumns{
        public static final String TABLE_NAME = "posicion";
        public static final String COLUMN_LUGAR = "lugar";
        public static final String COLUMN_LUGAR_LATITUD = "lat";
        public static final String COLUMN_lUGAR_LONGITUD = "long";
    }
    public  static class info implements  BaseColumns{
        public static final String TABLE_NAME = "info";
        public static final String COLUMN_CHECK = "chequeo";

    }
}

