package com.evernote.android.sample;

import java.text.SimpleDateFormat;
import java.text.Format;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;

import android.graphics.Bitmap;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.util.Log;

import com.evernote.edam.notestore.*;
import com.evernote.client.oauth.android.EvernoteSession;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.evernote.client.oauth.android.EvernoteSession;
import com.evernote.edam.notestore.*;
import com.evernote.edam.type.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.net.URL;

public class SnippetAdapter extends ArrayAdapter<NoteMetadata> {
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	int resource;
	String response;
	Context context;
	private EvernoteSession mEvernoteSession;
	DisplayImageOptions options;

	// Initialize adapter
	public SnippetAdapter(Context context, int resource,
			List<NoteMetadata> items, EvernoteSession mEvernoteSession) {
		super(context, resource, items);
		this.resource = resource;
		this.context = context;
		this.mEvernoteSession = mEvernoteSession;

		options = new DisplayImageOptions.Builder().cacheInMemory()
				.cacheOnDisc().showStubImage(R.drawable.traveljournal).build();

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout snippetView = (RelativeLayout) convertView;
		// Get the current alert object
		NoteMetadata snippetEntry = getItem(position);

		// Inflate the view
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			snippetView = (RelativeLayout) inflater.inflate(R.layout.snippet,
					null);
		} else {
			snippetView = (RelativeLayout) convertView;
		}

		// Get the text boxes from the listitem.xml file
		TextView snippetEvent = (TextView) snippetView
				.findViewById(R.id.snippetEvent);
		// TextView snippetDate =
		// (TextView)snippetView.findViewById(R.id.snippetDate);
		TextView snippetLocation = (TextView) snippetView
				.findViewById(R.id.snippetLocation);
		TextView snippetText = (TextView) snippetView
				.findViewById(R.id.snippetText);
		ImageView snippetPic = (ImageView) snippetView
				.findViewById(R.id.snippetPic);
		// imageLoader.displayImage("https://sandbox.evernote.com/shard/s1/thm/note/e669c090-d8b2-4324-9eae-56bd31c64af7.jpg?size=75",
		// snippetPic);
		imageLoader.displayImage(
				""
						+ mEvernoteSession.getmAuthenticationResult()
								.getWebApiUrlPrefix() + "thm/note/"
						+ snippetEntry.getGuid() + "?auth="
						+ mEvernoteSession.getAuthToken(), snippetPic, options);

		// Assign the appropriate data from our alert object above
		/*
		 * snippetEvent.setText(snippetEntry.getString("event"));
		 * snippetDate.setText(snippetEntry.getString("timestamp").substring(0,
		 * 10)); snippetLocation.setText(snippetEntry.getString("location"));
		 * snippetText.setText(snippetEntry.getString("notes")); JSONArray jp =
		 * null; JsonUploader uploader = new JsonUploader();
		 */
		try {
			snippetEvent.setText(snippetEntry.getTitle().toUpperCase());
			new ContentDler().execute(snippetEntry, snippetText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		snippetLocation.setText("Evernote Hack");

		// ThumbDler mThumbDler = new ThumbDler();
		try {
			// mThumbDler.execute(snippetEntry, snippetPic).get();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * try { jp = new
		 * JSONArray(uploader.execute("select * from photos where event='" +
		 * snippetEntry.getString("event") + "'", "0").get().toString());
		 * Log.e("log_tag ******", "select * from photos where event='" +
		 * snippetEntry.getString("event") + "'"); } catch (JSONException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (Exception e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */
		/*
		 * try { // String path = jp.getJSONObject(0).getString("path"); //
		 * String imageUrl = "http://kevinsutardji.com:8080/images/" + path;
		 * String imageUrl="http://lorempixel.com/100/80/"; Bitmap bitmap =
		 * BitmapFactory.decodeStream((InputStream)new
		 * URL(imageUrl).getContent()); Bitmap resized =
		 * Bitmap.createScaledBitmap(bitmap, 100, 80, false);
		 * snippetPic.setImageBitmap(bitmap); } catch (MalformedURLException e)
		 * { e.printStackTrace(); } catch (IOException e) { e.printStackTrace();
		 * } } catch (JSONException e) { e.printStackTrace();
		 */
		return snippetView;
	}

	private class ContentDler extends AsyncTask<Object, String, String> {
		TextView snippetText;

		protected void onPreExecute() {
			System.out.println("ContentDler");
		}

		@Override
		protected String doInBackground(Object... param) {
			NoteMetadata snippetEntry = (NoteMetadata) param[0];
			snippetText = (TextView) param[1];
			String output;
			try {
				output = android.text.Html.fromHtml(
						mEvernoteSession
								.createNoteStore()
								.getNote(mEvernoteSession.getAuthToken(),
										snippetEntry.getGuid(), true, true,
										false, false).getContent()).toString();
			} catch (Exception e) {
				e.printStackTrace();
				output = "Exception";
			}
			return output;

		}

		@Override
		protected void onPostExecute(String text) {
			System.out.println("Content Dl'd");
			snippetText.setText(text);
		}
	}

	private class ThumbDler extends AsyncTask<Object, String, Bitmap> {
		ImageView snippetPic;

		// private ProgressDialog dialog = new ProgressDialog(context);

		/*
		 * protected void onPreExecute() { this.dialog.setMessage("Loading");
		 * this.dialog.show(); }
		 */
		@Override
		protected Bitmap doInBackground(Object... param) {
			NoteMetadata mEvernoteNote = (NoteMetadata) param[0];
			snippetPic = (ImageView) param[1];
			Bitmap bm = Bitmap.createBitmap(75, 75, Bitmap.Config.ARGB_8888);
			// HttpClient httpClient = new DefaultHttpClient();
			// imageLoader = ImageLoader.getInstance();
			// imageLoader.displayImage("" +
			// mEvernoteSession.getmAuthenticationResult().getWebApiUrlPrefix()
			// + "thm/note/" + mEvernoteNote.getGuid()
			// +"?auth="+mEvernoteSession.getAuthToken(), snippetPic);
			imageLoader
					.displayImage(
							"https://sandbox.evernote.com/shard/s1/thm/note/e669c090-d8b2-4324-9eae-56bd31c64af7.jpg?size=75",
							snippetPic);

			/*
			 * try{ HttpPost httpPost = new HttpPost("" +
			 * mEvernoteSession.getmAuthenticationResult().getWebApiUrlPrefix()
			 * + "thm/note/" + mEvernoteNote.getGuid());
			 * httpPost.setHeader("Content-type",
			 * "application/x-www-form-urlencoded"); httpPost.setHeader("Host",
			 * "sandbox.evernote.com");
			 * 
			 * List<NameValuePair> nameValuePairs = new
			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
			 * BasicNameValuePair("auth", mEvernoteSession.getAuthToken()));
			 * nameValuePairs.add(new BasicNameValuePair("size", "75"));
			 * httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			 * 
			 * //HttpGet get = new
			 * HttpGet("https://sandbox.evernote.com/shard/s1/thm/res/" +
			 * mEvernoteNote.getGuid()
			 * +"?auth="+mEvernoteSession.getAuthToken()); HttpGet get = new
			 * HttpGet("" +
			 * mEvernoteSession.getmAuthenticationResult().getWebApiUrlPrefix()
			 * + "thm/note/" + mEvernoteNote.getGuid()
			 * +"?auth="+mEvernoteSession.getAuthToken());
			 * 
			 * // Handle the response from the web server HttpResponse response
			 * = httpClient.execute(get);
			 * 
			 * byte[] bytes = EntityUtils.toByteArray(response.getEntity());
			 * 
			 * bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); bm =
			 * Bitmap.createBitmap(bm, 0, 0, 75, 25);
			 * 
			 * return bm; } catch(Exception e) { e.printStackTrace(); }
			 */
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			/*
			 * if (dialog.isShowing()) { dialog.dismiss(); }
			 */
			// snippetPic.setImageBitmap(bitmap);
		}
	}
}
