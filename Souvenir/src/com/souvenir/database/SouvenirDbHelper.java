package com.souvenir.database;

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
      + COMMA_SEP + SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT
      + TEXT_TYPE + " )";

  public static final String SQL_DELETE_NOTE_TABLE = "DROP TABLE IF EXISTS "
      + SouvenirContract.SouvenirNote.TABLE_NAME_NOTE;

  // If you change the database schema, you must increment the database version.
  public static final int DATABASE_VERSION = 1;
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
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    // This database is only a cache for online data, so its upgrade policy is
    // to simply to discard the data and start over
    db.execSQL(SQL_DELETE_NOTE_TABLE);
    onCreate(db);
  }

  @Override
  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    onUpgrade(db, oldVersion, newVersion);
  }

}
