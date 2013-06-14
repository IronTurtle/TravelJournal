package edu.calpoly.android.apprater;

import edu.calpoly.android.apprater.AppView.OnAppChangeListener;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Binds a set of AppViews to a set of Apps using Cursors.
 */
public class AppCursorAdapter extends CursorAdapter {

	/** The OnAppChangeListener that should be connected to each of the
	 * AppViews created/managed by this Adapter. */
	private OnAppChangeListener m_listener;
	
	public AppCursorAdapter(Context context, Cursor cursor, int flags) {
		super(context, cursor, flags);
		this.m_listener = null;
	}

	/**
	 * Mutator method for changing the OnAppChangeListener.
	 * 
	 * @param listener
	 *            The OnAppChangeListener that will be notified when the
	 *            internal state of any Joke contained in one of this Adapters
	 *            AppViews is changed.
	 */
	public void setOnAppChangeListener(OnAppChangeListener mListener) {
		this.m_listener = mListener;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		App app = new App(cursor.getString(AppTable.APP_COL_NAME),
			cursor.getString(AppTable.APP_COL_INSTALLURI),
			cursor.getFloat(AppTable.APP_COL_RATING),
			cursor.getLong(AppTable.APP_COL_ID),
			cursor.getInt(AppTable.APP_COL_INSTALLED) > 0);
		((AppView)view).setOnAppChangeListener(null);
		((AppView)view).setApp(app);
		((AppView)view).setOnAppChangeListener(this.m_listener);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		App app = new App(cursor.getString(AppTable.APP_COL_NAME),
			cursor.getString(AppTable.APP_COL_INSTALLURI),
			cursor.getFloat(AppTable.APP_COL_RATING),
			cursor.getLong(AppTable.APP_COL_ID),
			cursor.getInt(AppTable.APP_COL_INSTALLED) > 0);
		AppView av = new AppView(context, app);
		av.setOnAppChangeListener(this.m_listener);
		return av;
	}
}
