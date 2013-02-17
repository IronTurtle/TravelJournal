package com.evernote.android.sample;

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

public class SnippetAdapter extends ArrayAdapter<NoteMetadata>
{

  int resource;
  String response;
  Context context;
  private EvernoteSession mEvernoteSession;

  //Initialize adapter
  public SnippetAdapter(Context context, int resource, List<NoteMetadata> items, EvernoteSession mEvernoteSession)
  {
    super(context, resource, items);
    this.resource = resource;
    this.context = context;
    this.mEvernoteSession = mEvernoteSession;
  }

  public View getView(int position, View convertView, ViewGroup parent)
  {
    RelativeLayout snippetView = (RelativeLayout) convertView;
    //Get the current alert object
    NoteMetadata snippetEntry = getItem(position);

    //Inflate the view
    if(convertView == null)
    {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      snippetView = (RelativeLayout) inflater.inflate(R.layout.snippet, null);
    }
    else
    {
      snippetView = (RelativeLayout) convertView;
    }


    //Get the text boxes from the listitem.xml file
    TextView snippetEvent = (TextView)snippetView.findViewById(R.id.snippetEvent);
    TextView snippetDate = (TextView)snippetView.findViewById(R.id.snippetDate);
    TextView snippetLocation = (TextView)snippetView.findViewById(R.id.snippetLocation);
    TextView snippetText = (TextView)snippetView.findViewById(R.id.snippetText);
    ImageView snippetPic = (ImageView)snippetView.findViewById(R.id.snippetPic);
    //Assign the appropriate data from our alert object above
    /*
       snippetEvent.setText(snippetEntry.getString("event"));
       snippetDate.setText(snippetEntry.getString("timestamp").substring(0, 10));
       snippetLocation.setText(snippetEntry.getString("location"));
       snippetText.setText(snippetEntry.getString("notes"));
       JSONArray jp = null;
       JsonUploader uploader = new JsonUploader();
     */

    snippetEvent.setText(snippetEntry.getTitle());
    snippetDate.setText(String.valueOf(snippetEntry.getUpdated()));
/*try{
    new ContentDler().execute(snippetEntry, snippetText);
}
catch(Exception e)
{
  e.printStackTrace();
}*/
  snippetLocation.setText("Evernote Hack");

    ThumbDler mThumbDler = new ThumbDler();
    try{
      mThumbDler.execute(snippetEntry, snippetPic).get();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    /*
       try
       {
       jp = new JSONArray(uploader.execute("select * from photos where event='" + snippetEntry.getString("event") + "'", "0").get().toString());
       Log.e("log_tag ******", "select * from photos where event='" + snippetEntry.getString("event") + "'");
       }
       catch (JSONException e)
       {
    // TODO Auto-generated catch block
    e.printStackTrace();
       }
       catch (InterruptedException e)
       {
    // TODO Auto-generated catch block
    e.printStackTrace();
       }
       catch (Exception e)
       {
    // TODO Auto-generated catch block
    e.printStackTrace();
       }
     */
    /*
       try
       {
    //   String path = jp.getJSONObject(0).getString("path");
    //  String imageUrl = "http://kevinsutardji.com:8080/images/" + path;
    String imageUrl="http://lorempixel.com/100/80/";
    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent());
    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 80, false);
    snippetPic.setImageBitmap(bitmap);
       }
       catch (MalformedURLException e)
       {
       e.printStackTrace();
       }
       catch (IOException e)
       {
       e.printStackTrace();
       }
  }
  catch (JSONException e)
  {
  e.printStackTrace();

     */   
    return snippetView;
  }

  private class ContentDler extends AsyncTask<Object, String, String>
  {
    TextView snippetText;

    @Override
    protected String doInBackground(Object... param)
    {
      NoteMetadata snippetEntry = (NoteMetadata) param[0];
      snippetText = (TextView) param[1];
      String output;
      try
      {
        output = mEvernoteSession.createNoteStore().getNote(mEvernoteSession.getAuthToken(), snippetEntry.getGuid(), true, true, false, false).getContent();
      }
      catch(Exception e)
      {
        e.printStackTrace();
        output = "Exception";
      }
      return output;

    }

    @Override
    protected void onPostExecute(String text) {
      snippetText.setText(text);
    }
  }

  private class ThumbDler extends AsyncTask<Object, String, Bitmap>
  {
    ImageView snippetPic;

    @Override
    protected Bitmap doInBackground(Object... param)
    {
      NoteMetadata mEvernoteNote = (NoteMetadata) param[0];
      snippetPic = (ImageView) param[1];
      Bitmap bm = Bitmap.createBitmap(75, 75, Bitmap.Config.ARGB_8888);
      HttpClient httpClient = new DefaultHttpClient();  
      try{
        HttpPost httpPost = new HttpPost("" + mEvernoteSession.getmAuthenticationResult().getWebApiUrlPrefix() + "thm/note/" + mEvernoteNote.getGuid());
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Host", "sandbox.evernote.com");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("auth", mEvernoteSession.getAuthToken()));
        nameValuePairs.add(new BasicNameValuePair("size", "75"));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        // Handle the response from the web server
        HttpResponse response = httpClient.execute(httpPost);  

        byte[] bytes = EntityUtils.toByteArray(response.getEntity());

        bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bm;
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      return bm;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      snippetPic.setImageBitmap(bitmap);
    }
  }
}
