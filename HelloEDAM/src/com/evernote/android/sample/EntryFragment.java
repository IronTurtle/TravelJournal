package com.evernote.android.sample;

import java.io.Serializable;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Resource;
import com.evernote.thrift.transport.TTransportException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EntryFragment extends ParentFragment implements OnClickListener,
    Serializable
{
  protected ImageLoader imageLoader = ImageLoader.getInstance();
  DisplayImageOptions options;

  // Note fields
  ImageView mImageView;
  EditText mTitle;
  EditText mLocation;
  EditText mEntry;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_entry, container, false);

    mImageView = (ImageView) view.findViewById(R.id.entry_image);
    mTitle = (EditText) view.findViewById(R.id.entry_title);
    mLocation = (EditText) view.findViewById(R.id.entry_location);
    mEntry = (EditText) view.findViewById(R.id.entry_entry);

    options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
        .showStubImage(R.drawable.traveljournal).build();

    getMetadata();
    //Bundle bundle = this.getActivity().getIntent().getExtras();

    //String guid = (String) bundle.get("guid");
    //String title = (String) bundle.get("title");
    //displayNote(guid, title);
    
    return view;
  }

  public void getMetadata()
  {
    int pageSize = 10;

    NoteFilter filter = new NoteFilter();
    filter.setOrder(NoteSortOrder.UPDATED.getValue());
    filter.setWords("-tag:itinerary*");

    NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
    spec.setIncludeTitle(true);

    try
    {
      mEvernoteSession
          .getClientFactory()
          .createNoteStoreClient()
          .findNotesMetadata(filter, 0, pageSize, spec,
              new OnClientCallback<NotesMetadataList>()
              {
                @Override
                public void onSuccess(NotesMetadataList notes)
                {
                  displayNote(notes.getNotes().get(0).getGuid(), notes
                      .getNotes().get(0).getTitle());
                }

                @Override
                public void onException(Exception exception)
                {

                }
              });
    } catch (TTransportException e)
    {

    }
  }

  public void displayNote(String guid, String title)
  {

    mTitle.setText(title.toUpperCase());

    try
    {
      mEvernoteSession.getClientFactory().createNoteStoreClient()
          .getNote(guid, true, true, true, true, new OnClientCallback<Note>()
          {
            @Override
            public void onSuccess(Note note)
            {
              String contents = note.getContent();

              mEntry.setText(android.text.Html.fromHtml(contents));

              Document doc = Jsoup.parse(contents);
              System.out.println("contents");
              System.out.println(contents);
              Elements divs = doc.getElementsByAttribute("hash");
              System.out.println("Images");
              for (Element div : divs)
              {
                System.out.println(div.attr("hash"));

                try
                {
                  mEvernoteSession
                      .getClientFactory()
                      .createNoteStoreClient()
                      .getResourceByHash(note.getGuid(),
                          EvernoteUtil.hexToBytes(div.attr("hash")), false,
                          false, false, new OnClientCallback<Resource>()
                          {

                            @Override
                            public void onSuccess(Resource data)
                            {
                              // TODO Auto-generated method stub
                              imageLoader.displayImage(
                                  ""
                                      + mEvernoteSession
                                          .getAuthenticationResult()
                                          .getWebApiUrlPrefix() + "res/"
                                      + data.getGuid() + "?auth="
                                      + mEvernoteSession.getAuthToken(),
                                  mImageView, options);
                            }

                            @Override
                            public void onException(Exception exception)
                            {
                              // TODO Auto-generated method stub

                            }

                          });
                } catch (TTransportException e)
                {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }

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
    } catch (TTransportException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId())
    {
    }
  }
}
