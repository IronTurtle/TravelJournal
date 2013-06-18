package com.souvenir.database;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

public class DatabaseActivity extends Activity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    // setContentView(R.layout.activity_database);

    String[] projection = { SouvenirContract.SouvenirNote._ID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT };

    Cursor c = this.getContentResolver().query(
        Uri.parse(SouvenirContentProvider.CONTENT_URI + "/note/new"),
        projection, null, null, null);
    String resultString1 = "";
    c.moveToFirst();
    for (int i = 0; i < c.getCount(); i++)
    {
      resultString1 += String
          .valueOf(c.getInt(c
              .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID)))
          + "\n";
      if (i < c.getCount() - 1)
        c.moveToNext();
    }
    // TextView textView1 = (TextView)findViewById(R.id.db_result);
    // textView1 = (TextView)findViewById(R.id.db_result);
    // textView1.setText(resultString1);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    // getMenuInflater().inflate(R.menu.activity_database, menu);
    return true;
  }

  private Cursor queryAll(SQLiteDatabase db)
  {
    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    String[] projection = { SouvenirContract.SouvenirNote._ID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT };

    // How you want the results sorted in the resulting Cursor
    String sortOrder = SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID
        + " ASC";

    Cursor c = db.query(SouvenirContract.SouvenirNote.TABLE_NAME_NOTE, // The
                                                                       // table
                                                                       // to
                                                                       // query
        projection, // The columns to return
        null, // The columns for the WHERE clause
        null, // The values for the WHERE clause
        null, // don't group the rows
        null, // don't filter by row groups
        sortOrder // The sort order
        );

    return c;
  }

}
