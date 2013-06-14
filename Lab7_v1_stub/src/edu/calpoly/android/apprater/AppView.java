package edu.calpoly.android.apprater;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppView extends RelativeLayout {

	/** The data behind this View. Contains the app's information. */
	private App m_app;
	
	/** The container ViewGroup for all other Views in an AppView.
	 * Used to set the view's background color dynamically. */
	private RelativeLayout m_vwContainer;
	
	/** Indicates whether or not the App is installed.
	 * This must be set to non-interactive in the XML layout file. */
	private CheckBox m_vwInstalledCheckBox;
	
	/** Shows the user's current rating for the application. */
	private RatingBar m_vwAppRatingBar;
	
	/** The name of the App. */
	private TextView m_vwAppName;
	
	/** The context this view is in. Used for checking install status. */
	private Context context;

	/** Interface between this AppView and the database it's stored in. */
	private OnAppChangeListener m_onAppChangeListener;
	
	public AppView(Context context, App app) {
		super(context);
		//TODO
	}
	
	public App getApp() {
		return m_app;
	}
	
	public void setApp(App app) {
		//TODO
	}
	
	/**
	 * Mutator method for changing the OnAppChangeListener object this AppView
	 * notifies when the state its underlying App object changes.
	 * 
	 * It is possible and acceptable for m_onAppChangeListener to be null, you
	 * should allow for this.
	 * 
	 * @param listener
	 *            The OnAppChangeListener object that should be notified when
	 *            the underlying App changes state.
	 */
	public void setOnAppChangeListener(OnAppChangeListener listener) {
		this.m_onAppChangeListener = listener;
	}

	/**
	 * This method should always be called after the state of m_app is changed.
	 * 
	 * It is possible and acceptable for m_onAppChangeListener to be null, you
	 * should test for this.
	 */
	protected void notifyOnAppChangeListener() {
		if (m_onAppChangeListener != null) {
			m_onAppChangeListener.onAppChanged(this, m_app);
		}
	}
	
	/**
	 * Interface definition for a callback to be invoked when the underlying
	 * App is changed in this AppView object.
	 */
	public static interface OnAppChangeListener {

		/**
		 * Called when the underlying App in an AppView object changes state.
		 * 
		 * @param view
		 *            The AppView in which the App was changed.
		 * @param app
		 *            The App that was changed.
		 */
		public void onAppChanged(AppView view, App app);
	}
}