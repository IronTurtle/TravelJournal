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
import org.json.*;

import com.evernote.edam.notestore.*;

public class SnippetAdapter extends ArrayAdapter<NoteMetadata>
{

  int resource;
  String response;
  Context context;
  //Initialize adapter
  public SnippetAdapter(Context context, int resource, List<NoteMetadata> items)
  {
    super(context, resource, items);
    this.resource = resource;
    this.context = context;
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

      snippetText.setText(snippetEntry.getTitle());
      snippetDate.setText("test");


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

         try
         {
         String path = jp.getJSONObject(0).getString("path");
         String imageUrl = "http://kevinsutardji.com:8080/images/" + path;
      //String imageUrl="http://lorempixel.com/100/80/";
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
    }
       */
      return snippetView;
  }

}
