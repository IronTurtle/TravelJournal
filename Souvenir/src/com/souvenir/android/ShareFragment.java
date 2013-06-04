package com.souvenir.android;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class ShareFragment extends ParentFragment implements OnClickListener
{
	private Button shareButton;
	private static String TAG = "Facebook";
	
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "c39ac2d0bba70a453f64550f3c18a830";
	private boolean pendingPublishReauthorization = false;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_share, container, false);
		setHasOptionsMenu(true);
		
		System.out.println("Share Button Clicked...");
		publishStory();  
		
		if (savedInstanceState != null) {
		    pendingPublishReauthorization = 
		        savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
		}
		
		return view;

	}
	
	//unused
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	    	shareButton.setVisibility(View.VISIBLE);
	    	if (pendingPublishReauthorization && 
	    	        state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
	    	    pendingPublishReauthorization = false;
	    	    publishStory();
	    	}
	    } else if (state.isClosed()) {
	        shareButton.setVisibility(View.INVISIBLE);
	    }
	}
	
	private void publishStory() {
	    Session session = Session.getActiveSession();
	    System.out.println("PUPBLISHING STORY: " + session);
	    if (session != null){

	        // Check for publish permissions    
	        List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	            pendingPublishReauthorization = true;
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	            //return;
	        }
	        String title = getActivity().getIntent().getExtras().getStringArray("NOTE")[0];
	        String loc = getActivity().getIntent().getExtras().getStringArray("NOTE")[1];
	        String entry = getActivity().getIntent().getExtras().getStringArray("NOTE")[2];
	        Bundle postParams = new Bundle();
	        postParams.putString("name", "Souvenir @ Evernote Hackathon: " + title);
	        postParams.putString("caption", loc);
	        postParams.putString("description", entry);
	        postParams.putString("link", "https://www.hackerleague.org/hackathons/honda-and-evernote-hackathon/hacks/souvenir");
	        postParams.putString("picture", "http://oi40.tinypic.com/mlpooo.jpg");	        
	        System.out.println("***BUNDLED UP!!");
	        
	        Request.Callback callback= new Request.Callback() {
	            public void onCompleted(Response response) {
	                JSONObject graphResponse = response
	                                           .getGraphObject()
	                                           .getInnerJSONObject();
	                String postId = null;
	                try {
	                    postId = graphResponse.getString("id");
	                } catch (JSONException e) {
	                    Log.i(TAG,
	                        "JSON error "+ e.getMessage());
	                }
	                FacebookRequestError error = response.getError();
	                if (error != null) {
	                    Toast.makeText(getActivity()
	                         .getApplicationContext(),
	                         error.getErrorMessage(),
	                         Toast.LENGTH_SHORT).show();
	                    } else {
	                        //Toast.makeText(getActivity().getApplicationContext(),postId,Toast.LENGTH_SHORT).show();
	                }
	                ((ShareActivity)getActivity()).finish();
	    			getFragmentManager().popBackStack();
	            }
	        };

	        Request request = new Request(session, "me/feed", postParams, 
	                              HttpMethod.POST, callback);
	        System.out.println("*** NEW REQUEST");
	        RequestAsyncTask task = new RequestAsyncTask(request);
	        System.out.println("***NEW TASK");
	        task.execute();
	    }

	}
	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
	    
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}