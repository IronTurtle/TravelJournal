package com.souvenir.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SouvenirDbHelper extends SQLiteOpenHelper
{

  private static final String TEXT_TYPE = " TEXT";
  private static final String INTEGER_TYPE = " INTEGER";
  private static final String COMMA_SEP = ",";
  public static final String SQL_CREATE_NOTE_TABLE = "CREATE TABLE "
      + SouvenirContract.SouvenirNote.TABLE_NAME_NOTE + " ("
      + SouvenirContract.SouvenirNote._ID + INTEGER_TYPE + " PRIMARY KEY,"
      + SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID + INTEGER_TYPE
      + " UNIQUE" + COMMA_SEP
      + SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE + TEXT_TYPE
      + COMMA_SEP + SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_LOCATION
      + TEXT_TYPE + COMMA_SEP
      + SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT + TEXT_TYPE
      + COMMA_SEP + SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_SYNC_NUM
      + INTEGER_TYPE + COMMA_SEP
      + SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_DIRTY + " BOOLEAN"
      + COMMA_SEP + SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_ISSET
      + INTEGER_TYPE + COMMA_SEP
      + SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_MODIFY_DATE
      + INTEGER_TYPE + COMMA_SEP
      + SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CREATE_DATE
      + INTEGER_TYPE + " )";
  public static final String SQL_CREATE_RESOURCE_TABLE = "CREATE TABLE "
      + SouvenirContract.SouvenirResource.TABLE_NAME_RESOURCE + " ("
      + SouvenirContract.SouvenirResource._ID + INTEGER_TYPE + " PRIMARY KEY,"
      + SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_CAPTION
      + TEXT_TYPE + COMMA_SEP
      + SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_GUID + TEXT_TYPE
      + COMMA_SEP + SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_HASH
      + TEXT_TYPE + COMMA_SEP
      + SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_LOCATION
      + TEXT_TYPE + COMMA_SEP
      + SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_MIME + TEXT_TYPE
      + COMMA_SEP
      + SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_NOTE_ID
      + INTEGER_TYPE + COMMA_SEP
      + SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_PATH + TEXT_TYPE
      + " NOT NULL " + " )";
  public static final String SQL_CREATE_TRIP_TABLE = "CREATE TABLE "
      + SouvenirContract.SouvenirTrip.TABLE_NAME_TRIP + " ("
      + SouvenirContract.SouvenirTrip._ID + INTEGER_TYPE + " PRIMARY KEY,"
      + SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_GUID + TEXT_TYPE
      + COMMA_SEP + SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_NAME
      + TEXT_TYPE + COMMA_SEP
      + SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_SYNC_NUM + INTEGER_TYPE
      + COMMA_SEP + SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_DIRTY
      + INTEGER_TYPE + " BOOLEAN" + " )";

  public static final String SQL_DELETE_NOTE_TABLE = "DROP TABLE IF EXISTS "
      + SouvenirContract.SouvenirNote.TABLE_NAME_NOTE;
  public static final String SQL_DELETE_RESOURCE_TABLE = "DROP TABLE IF EXISTS "
      + SouvenirContract.SouvenirResource.TABLE_NAME_RESOURCE;
  public static final String SQL_DELETE_TRIP_TABLE = "DROP TABLE IF EXISTS "
      + SouvenirContract.SouvenirTrip.TABLE_NAME_TRIP;

  // If you change the database schema, you must increment the database version.
  public static final int DATABASE_VERSION = 2;
  public static final String DATABASE_NAME = "Souvenir.db";

  public SouvenirDbHelper(Context context, String DatabaseName,
      CursorFactory factory, int version)
  {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db)
  {
    db.execSQL(SQL_CREATE_NOTE_TABLE);
    db.execSQL(SQL_CREATE_RESOURCE_TABLE);
    db.execSQL(SQL_CREATE_TRIP_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    // This database is only a cache for online data, so its upgrade policy is
    // to simply to discard the data and start over
    db.execSQL(SQL_DELETE_NOTE_TABLE);
    db.execSQL(SQL_DELETE_RESOURCE_TABLE);
    db.execSQL(SQL_DELETE_TRIP_TABLE);
    onCreate(db);
  }

  @Override
  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    onUpgrade(db, oldVersion, newVersion);
  }

}
