package com.souvenir.database;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Class that provides content from a SQLite database to the application.
 * Provides app information to a ListView through a CursorAdapter. The database
 * stores app data in a two-dimensional table, where each row is an app and each
 * column is a property of an app (ID, name, rating, install URI, whether or not
 * the app is installed on the device).
 * 
 * Note that CursorLoaders require a ContentProvider, which is why this
 * application wraps a SQLite database into a content provider instead of
 * managing the database<-->application transactions manually.
 */
public class SouvenirContentProvider extends ContentProvider
{
  public static class DatabaseConstants
  {
    public static String NOTE = "note/";
    public static String GET_NOTE = "note/#";
    public static String NOTE_RESOURCES = "res/#";
  }

  /*****************/
  /** STATIC DATA **/
  /*****************/

  /** The app database. */
  private SouvenirDbHelper database;

  /** Values for the URIMatcher. */
  public static final int NOTE = 1;
  public static final int GET_NOTE = NOTE + 1;
  public static final int NOTE_RES = NOTE + 2;

  /** The authority for this content provider. */
  private static final String AUTHORITY = "com.souvenir.database";

  /**
   * The database table to read from and write to, and also the root path for
   * use in the URI matcher. This value is essentially a pointer for
   * applications trying to use the content provider; it points accessors to a
   * database table, which contains a two-dimensional array representation of
   * the database filled with rows of apps whose columns contain app data.
   */
  private static final String BASE_PATH = "tables";

  /**
   * The default root content URI. May be extended to perform various operations
   * on the database.
   */
  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
      + "/");

  /**
   * Matches content URIs requested by accessing applications with possible
   * expected content URI formats to take specific actions in this provider.
   */
  private static final UriMatcher s_URIMatcher;
  static
  {
    s_URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    s_URIMatcher.addURI(AUTHORITY, DatabaseConstants.NOTE, NOTE);
    s_URIMatcher.addURI(AUTHORITY, DatabaseConstants.GET_NOTE, GET_NOTE);
    s_URIMatcher.addURI(AUTHORITY, DatabaseConstants.NOTE_RESOURCES, NOTE_RES);
  }

  /**
   * Initialize your SQLiteDatabase here. Make sure to return the correct
   * boolean value.
   */
  @Override
  public boolean onCreate()
  {
    this.database = new SouvenirDbHelper(getContext(),
        SouvenirDbHelper.DATABASE_NAME, null, SouvenirDbHelper.DATABASE_VERSION);
    return false;
  }

  @Override
  public String getType(Uri uri)
  {
    return null;
  }

  /**
   * Fetches rows from the app table. Given a specified URI that contains the
   * base path and a sorting order, returns a list of apps from the app table
   * matching that order in the form of a Cursor.<br>
   * <br>
   * 
   * Overrides the built-in version of <b>query(...)</b> provided by
   * ContentProvider.<br>
   * <br>
   * 
   * This method is like the JokeContentProvider.query(...) method from Lab 4.
   */
  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder)
  {

    // Use a helper class to perform a query for us.
    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

    // Make sure the projection is proper before querying.
    checkColumns(projection);

    // Set up helper to query our app table.
    queryBuilder.setTables(SouvenirContract.SouvenirNote.TABLE_NAME_NOTE);

    String orderBy = null;

    // Match the passed-in URI to an expected URI format.
    int uriType = s_URIMatcher.match(uri);
    System.out.println("URI Type: " + uriType);

    switch (uriType)
    {
    case NOTE:

      // Default sort order if none specified
      if (sortOrder == null || TextUtils.isEmpty(sortOrder))
      {
        orderBy = SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID;
      }
      // Specified sort order
      else
      {
        orderBy = sortOrder;
      }

      break;

    case NOTE_RES:
      // Note the escaped '"' needed when adding a String to the whereclause.
      queryBuilder
          .appendWhere(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID
              + "= \"" + uri.getLastPathSegment() + "\"");
      break;

    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    // Perform the database query.
    SQLiteDatabase db = this.database.getWritableDatabase();
    Cursor cursor = queryBuilder.query(db, projection, selection,
        selectionArgs, null, null, orderBy);

    // Set the cursor to automatically alert listeners for content/view
    // refreshing.
    cursor.setNotificationUri(getContext().getContentResolver(), uri);

    return cursor;
  }

  /**
   * Inserts an app into the apptable. Given a specific URI that contains an app
   * and the values of that app, writes a new row in the table filled with that
   * app's information and gives the app a new ID, then returns a URI containing
   * the ID of the inserted app.<br>
   * <br>
   * 
   * Overrides the built-in version of <b>insert(...)</b> provided by
   * ContentProvider.<br>
   * <br>
   * 
   * This method is like the JokeContentProvider.insert(...) method from Lab 4.
   */
  @Override
  public Uri insert(Uri uri, ContentValues values)
  {

    // Open the database for writing.
    SQLiteDatabase sqlDB = this.database.getWritableDatabase();

    // Will eventually contain the ID of the inserted app.
    long id = 0;

    // Match the passed-in URI to an expected URI format.
    int uriType = s_URIMatcher.match(uri);

    switch (uriType)
    {

    // Expects an App ID, but we will do nothing with the passed-in ID since
    // the database will automatically handle ID assignment and incrementation.
    // IMPORTANT: App ID cannot be set to -1 in passed-in URI; -1 is not
    // interpreted
    // as a numerical value by the URIMatcher.
    case NOTE:

      // Perform the database insert, placing the app at the bottom of the
      // table.
      id = sqlDB.insert(SouvenirContract.SouvenirNote.TABLE_NAME_NOTE, null,
          values);
      break;

    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    // Only alert if app was successfully inserted
    if (id != -1)
    {

      // Alert any watchers of an underlying data change for content/view
      // refreshing.
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return Uri.parse("/" + id);
  }

  /**
   * Removes a row from the app table. Given a specific URI containing an app
   * ID, removes rows in the table that match the ID and returns the number of
   * rows removed. Since IDs are automatically incremented on insertion, this
   * will only ever remove a single row from the app table.<br>
   * <br>
   * 
   * Overrides the built-in version of <b>delete(...)</b> provided by
   * ContentProvider.<br>
   * <br>
   * 
   * This method is like the JokeContentProvider.delete(...) method from Lab 4.
   */
  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs)
  {

    // Open the database for writing. Deletion is a write operation.
    SQLiteDatabase sqlDB = database.getWritableDatabase();

    // Keep track of the number of deleted rows for the return value.
    int rowsDeleted = 0;

    // Match the passed-in URI to an expected URI format.
    int uriType = s_URIMatcher.match(uri);
    switch (uriType)
    {

    // Remove all rows from the app table with the matching ID.
    case NOTE:
      rowsDeleted = sqlDB.delete(SouvenirContract.SouvenirNote.TABLE_NAME_NOTE,
          null, null);
      break;

    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    // Only alert if rows were actually removed.
    if (rowsDeleted > 0)
    {

      // Alert any watchers of an underlying data change for content/view
      // refreshing.
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return rowsDeleted;
  }

  /**
   * Updates a row in the app table. Given a specific URI containing an app ID
   * and the new app values, updates the values in the row with the matching ID
   * in the table. Since IDs are automatically incremented on insertion, this
   * will only ever update a single row in the app table.<br>
   * <br>
   * 
   * Overrides the built-in version of <b>update(...)</b> provided by
   * ContentProvider.<br>
   * <br>
   * 
   * This method is like the JokeContentProvider.update(...) method from Lab 4.
   */
  @Override
  public int update(Uri uri, ContentValues values, String selection,
      String[] selectionArgs)
  {

    // Open the database for writing.
    SQLiteDatabase sqlDB = database.getWritableDatabase();

    // Keep track of the number of updated rows for the return value.
    int rowsUpdated = 0;

    // Match the passed-in URI to an expected URI format.
    int uriType = s_URIMatcher.match(uri);
    switch (uriType)
    {

    // Update a row in the app table with the matching ID.
    case GET_NOTE:
      String id = uri.getLastPathSegment();

      // Perform the actual update in the table.
      if (!TextUtils.isEmpty(selection))
      {
        rowsUpdated = sqlDB.update(
            SouvenirContract.SouvenirNote.TABLE_NAME_NOTE, values,
            SouvenirContract.SouvenirNote._ID + "=" + id + " AND " + selection,
            null);
      }
      else
      {
        rowsUpdated = sqlDB.update(
            SouvenirContract.SouvenirNote.TABLE_NAME_NOTE, values,
            SouvenirContract.SouvenirNote._ID + "=" + id, null);
      }

      break;

    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    // Only alert if rows were actually removed.
    if (rowsUpdated > 0)
    {

      // Alert any watchers of an underlying data change for content/view
      // refreshing.
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return rowsUpdated;
  }

  /**
   * Verifies the correct set of columns to return data from when performing a
   * query.
   * 
   * @param projection
   *          The set of columns about to be queried.
   */
  private void checkColumns(String[] projection)
  {
    if (true)
      return;
    String[] available = { SouvenirContract.SouvenirNote._ID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_LOCATION };

    if (projection != null)
    {
      HashSet<String> requestedColumns = new HashSet<String>(
          Arrays.asList(projection));
      HashSet<String> availableColumns = new HashSet<String>(
          Arrays.asList(available));

      if (!availableColumns.containsAll(requestedColumns))
      {
        throw new IllegalArgumentException("Unknown columns in projection");
      }
    }
  }
}
