package com.evernote.android.sample;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.*;
import com.evernote.edam.type.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("DefaultLocale")
public class SnippetAdapter extends ArrayAdapter<NoteMetadata>
{
  protected ImageLoader imageLoader = ImageLoader.getInstance();

  int resource;
  String response;
  Context context;
  private EvernoteSession mEvernoteSession;
  DisplayImageOptions options;

  // Initialize adapter
  public SnippetAdapter(Context context, int resource,
      List<NoteMetadata> items, EvernoteSession mEvernoteSession)
  {
    super(context, resource, items);
    this.resource = resource;
    this.context = context;
    this.mEvernoteSession = mEvernoteSession;

    options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
        .showStubImage(R.drawable.traveljournal).build();

  }

  public View getView(int position, View convertView, ViewGroup parent)
  {
    
    RelativeLayout snippetView = (RelativeLayout) convertView;
    // Get the current alert object
    NoteMetadata snippetEntry = getItem(position);

    // Inflate the view
    if (convertView == null)
    {
      LayoutInflater inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      snippetView = (RelativeLayout) inflater.inflate(R.layout.snippet, null);
    } else
    {
      snippetView = (RelativeLayout) convertView;
    }

    // Get the text boxes from the listitem.xml file
    TextView snippetEvent = (TextView) snippetView
        .findViewById(R.id.snippetEvent);
    TextView snippetLocation = (TextView) snippetView
        .findViewById(R.id.snippetLocation);
    final TextView snippetText = (TextView) snippetView
        .findViewById(R.id.snippetText);
    ImageView snippetPic = (ImageView) snippetView
        .findViewById(R.id.snippetPic);
    snippetText.setText("");

    imageLoader.displayImage(
        "" + mEvernoteSession.getAuthenticationResult().getWebApiUrlPrefix()
            + "thm/note/" + snippetEntry.getGuid() + "?auth="
            + mEvernoteSession.getAuthToken(), snippetPic, options);

    snippetEvent.setText(snippetEntry.getTitle().toUpperCase());
    try
    {
      mEvernoteSession
          .getClientFactory()
          .createNoteStoreClient()
          .getNote(snippetEntry.getGuid(), true, true, true, true,
              new OnClientCallback<Note>()
              {
                @Override
                public void onSuccess(Note note)
                {
                  snippetText.setText(android.text.Html.fromHtml(note
                      .getContent()));
                  // removeDialog(DIALOG_PROGRESS);
                  // Toast.makeText(getApplicationContext(),
                  // R.string.msg_image_saved, Toast.LENGTH_LONG).show();
                  // notes = data;
                }

                @Override
                public void onException(Exception exception)
                {
                  
                }
              });
    } catch (Exception e)
    {
      e.printStackTrace();
    }

    snippetLocation.setText("Evernote Hack");

    return snippetView;
  }
}
