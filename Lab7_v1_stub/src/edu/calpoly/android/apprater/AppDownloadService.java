package edu.calpoly.android.apprater;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Service that performs app information downloading, performing the check again
 * occasionally over time. It adds an application to the list of apps and is also
 * responsible for telling the BroadcastReceiver when it has done so.
 */
public class AppDownloadService {

	/** The ID for the Notification that is generated when a new App is added. */
	public static final int NEW_APP_NOTIFICATION_ID = 1;
	
	/** The Timer thread which will execute the check for new Apps. Acts like a Thread
	 * that can be told to start at a specific time and/or at specific time intervals. */
	private Timer m_updateTimer;
	
	/** The TimerTask which encapsulates the logic that will check for new Apps. This ends
	 * up getting run by the Timer in the same way that a Thread runs a Runnable. */
	private TimerTask m_updateTask;

	/** The time frequency at which the service should check the server for new Apps. */
	private static final long UPDATE_FREQUENCY = 10000L;

	/** A String containing the URL from which to download the list of all Apps. */
	public static final String GET_APPS_URL = "http://www.simexusa.com/aac/getAll.php";

	/**
	 * Note that the constructor that takes a String will NOT be properly instantiated.
	 * Use the constructor that takes no parameters instead, and pass in a String that
	 * contains the name of the service to the super() call.
	 */
	public AppDownloadService() {
		//TODO
	}

	/**
	 * This method downloads all of the Apps from the App server. For each App,
	 * it checks the AppContentProvider to see if it has already been downloaded
	 * before. If it is new, then it adds it to the AppContentProvider by
	 * calling addNewApp.
	 */
	private void getAppsFromServer() {
		//TODO
	}

	/**
	 * This method adds a new App to the AppContentProvider.
	 * 
	 * @param app
	 *            The new App object to add to the ContentProvider.
	 */
	private void addNewApp(App app) {
		//TODO
	}

	/**
	 * This method broadcasts an intent with a specific Action String. This method should be
	 * called when a new App has been downloaded and added successfully.
	 */
	private void announceNewApp() {
		//TODO
	}
}
