package edu.calpoly.android.apprater;

/**
 * A POJO (Plain Old Java Object) that represents an App in data form.
 */
public class App {

	/** The name of the App. */
	private String m_strAppName;
	
	/** The URI that corresponds to a location for information on the App in the Android
	 * Market/Play Store. */
	private String m_strInstallURI;
	
	/** The App's user-given rating. Separate from the App's rating in the Google Play Store. */
	private float m_nRating;
	
	/** The App's ID. */
	private long m_nID;
	
	/** Whether or not the App is installed on the user's device. */
	private boolean m_bInstalled;
	
	public static final long NO_ID = 0;
	public static final int UNRATED = 0;
	
	public App(String appName, String installURI, float rating, long id, boolean installed) {
		super();
		m_strAppName = appName;
		m_strInstallURI = installURI;
		m_nRating = rating;
		m_nID = id;
		m_bInstalled = installed;
	}
	
	public App(String appName, String installURI) {
		super();
		m_strAppName = appName;
		m_strInstallURI = installURI;
		m_nRating = UNRATED;
		m_nID = NO_ID;
		m_bInstalled = false;
	}
	
	public float getRating() {
		return m_nRating;
	}
	public void setRating(float rating) {
		m_nRating = rating;
	}
	
	public boolean isInstalled() {
		return m_bInstalled;
	}
	public void setInstalled(boolean installed) {
		m_bInstalled = installed;
	}
	
	public long getID() {
		return m_nID;
	}
	public void setID(long id) {
		m_nID = id;
	}
	
	public String getName() {
		return m_strAppName;
	}
	public String getInstallURI() {
		return m_strInstallURI;
	}
}
