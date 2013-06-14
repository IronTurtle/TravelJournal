package com.souvenir.database;

import android.provider.BaseColumns;

public class SouvenirContract
{

  public static abstract class SouvenirNote implements BaseColumns
  {
    public static final String TABLE_NAME_NOTE = "note";
    public static final String COLUMN_NAME_NOTE_GUID = "note_id";
    public static final String COLUMN_NAME_NOTE_TITLE = "note_title";
    public static final String COLUMN_NAME_NOTE_CONTENT = "note_content";
    public static final String COLUMN_NAME_NOTE_LOCATION = "note_location";
    public static final String COLUMN_NAME_NOTE_SYNC_NUM = "note_sync_num";
    public static final String COLUMN_NAME_NOTE_TRIP_ID = "note_trip_id";
    public static final String COLUMN_NAME_NOTE_CREATE_DATE = "note_create_date";
    public static final String COLUMN_NAME_NOTE_MODIFY_DATE = "note_modify_date";
    public static final String COLUMN_NAME_NOTE_START_DATE = "note_start_date";
    public static final String COLUMN_NAME_NOTE_END_DATE = "note_end_date";
  }

}
