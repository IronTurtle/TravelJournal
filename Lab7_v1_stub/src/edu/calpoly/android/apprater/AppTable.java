package edu.calpoly.android.apprater;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Class that provides helpful database table accessor variables and manages
 * basic required database functionality.  
 */
public class AppTable {
	
	/** App table in the database. */
	public static final String DATABASE_TABLE_APP = "app_table";
	
	//App table column names and IDs for database access:
	/** String for the auto-incremented App ID. IDs start at 1 in the table. */
	public static final String APP_KEY_ID = "_id";
	/** Column index for the auto-incremented App ID. */
	public static final int APP_COL_ID = 0;
	
	/** String for the App name. */
	public static final String APP_KEY_NAME = "name";
	/** Column index for the App name. */
	public static final int APP_COL_NAME = APP_COL_ID + 1;
	
	/** String for the App rating. */
	public static final String APP_KEY_RATING = "rating";
	/** Column index for the App rating. */
	public static final int APP_COL_RATING = APP_COL_ID + 2;
	
	/** String for the App's Market URI. */
	public static final String APP_KEY_INSTALLURI = "install_uri";
	/** Column index for the App's Market URI. */
	public static final int APP_COL_INSTALLURI = APP_COL_ID + 3;
	
	/** String for the App install status. 0 for not installed, 1 for installed. */
	public static final String APP_KEY_INSTALLED = "installed";
	/** Column index for the App install status. */
	public static final int APP_COL_INSTALLED = APP_COL_ID + 4;
	
	/** SQLite database creation statement. Auto-increments IDs of inserted Apps.
	 * App IDs are set after insertion into the database. */
	public static final String DATABASE_CREATE = "create table " + DATABASE_TABLE_APP + " (" + 
			APP_KEY_ID + " integer primary key autoincrement, " + 
			APP_KEY_NAME + " text not null unique, " + 
			APP_KEY_RATING + " real not null, " + 
			APP_KEY_INSTALLURI + " text not null unique, " + 
			APP_KEY_INSTALLED + " integer not null );";
	
	/** The orderBy String for query results. This orders query results first by install
	 * status, then by rating, then finally by name. Feel free to change it to something else. */
	public static final String ORDER_BY_STRING = APP_KEY_INSTALLED + ", " + APP_KEY_RATING +
		", " + APP_KEY_NAME;
	
	/** SQLite database table removal statement. Only used if upgrading database. */
	public static final String DATABASE_DROP = "drop table if exists " + DATABASE_TABLE_APP;
	
	/**
	 * Initializes the database.
	 * 
	 * @param database
	 * 				The database to initialize.	
	 */
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	/**
	 * Upgrades the database to a new version.
	 * 
	 * @param database
	 * 					The database to upgrade.
	 * @param oldVersion
	 * 					The old version of the database.
	 * @param newVersion
	 * 					The new version of the database.
	 */
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(AppTable.class.getName(), "Upgrading database from version " + oldVersion + "to " + newVersion);
		database.execSQL(DATABASE_DROP);
		onCreate(database);
	}
}
