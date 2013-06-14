package edu.calpoly.android.apprater;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class that hooks up to the AppContentProvider for initialization and
 * maintenance. Uses AppTable for assistance.
 */
public class AppDatabaseHelper extends SQLiteOpenHelper {

	/** The name of the database. */
	public static final String DATABASE_NAME = "appdatabase.db";
	
	/** The starting database version. */
	public static final int DATABASE_VERSION = 1;
	
	public AppDatabaseHelper(Context context, String name,
		CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		AppTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		AppTable.onUpgrade(db, oldVersion, newVersion);
	}
}
