package com.souvenir.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SnippetView extends RelativeLayout
{

  private SNote m_app;

  /**
   * The container ViewGroup for all other Views in an AppView. Used to set the
   * view's background color dynamically.
   */
  private RelativeLayout m_vwContainer;

  /** The context this view is in. Used for checking install status. */
  private Context context;

  /** Interface between this AppView and the database it's stored in. */
  // private OnAppChangeListener m_onAppChangeListener;

  private TextView snippetText;

  private ImageView snippetPic;

  private TextView snippetLocation;

  private TextView snippetEvent;

  public SnippetView(Context context, SNote note)
  {
    super(context);
    this.context = context;
    View view = LayoutInflater.from(context).inflate(R.layout.snippet, this);
    snippetEvent = (TextView) view.findViewById(R.id.snippetEvent);
    snippetLocation = (TextView) view.findViewById(R.id.snippetLocation);
    snippetText = (TextView) view.findViewById(R.id.snippetText);
    snippetPic = (ImageView) view.findViewById(R.id.snippetPic);
    setSNote(note);
    // m_onAppChangeListener = null;
  }

  public void setSNote(SNote note)
  {
    this.m_app = note;

    snippetText.setText("");

    // imageLoader.displayImage(
    // "" + mEvernoteSession.getAuthenticationResult().getWebApiUrlPrefix()
    // + "thm/note/" + snippetEntry.getGuid() + "?auth="
    // + mEvernoteSession.getAuthToken(), snippetPic, options);

    // String location = snippetEntry.getAttributes().getPlaceName();
    // if (location == null)
    // {
    // location = String.valueOf((snippetEntry.getAttributes().getLatitude())
    // + String.valueOf(snippetEntry.getAttributes().getLongitude()));
    // }

    snippetLocation.setText(note.getNoteLocation());
    snippetEvent.setText(note.getNoteTitle());
    snippetText.setText(android.text.Html.fromHtml(note.getNoteContent())
        .toString());
  }

  // /**
  // * Mutator method for changing the OnAppChangeListener object this AppView
  // * notifies when the state its underlying App object changes.
  // *
  // * It is possible and acceptable for m_onAppChangeListener to be null, you
  // * should allow for this.
  // *
  // * @param listener
  // * The OnAppChangeListener object that should be notified when the
  // * underlying App changes state.
  // */
  // public void setOnAppChangeListener(OnAppChangeListener listener)
  // {
  // this.m_onAppChangeListener = listener;
  // }
  //
  // /**
  // * This method should always be called after the state of m_app is changed.
  // *
  // * It is possible and acceptable for m_onAppChangeListener to be null, you
  // * should test for this.
  // */
  // protected void notifyOnAppChangeListener()
  // {
  // if (m_onAppChangeListener != null)
  // {
  // m_onAppChangeListener.onAppChanged(this, m_app);
  // }
  // }
  //
  // /**
  // * Interface definition for a callback to be invoked when the underlying App
  // * is changed in this AppView object.
  // */
  // public static interface OnAppChangeListener
  // {
  //
  // /**
  // * Called when the underlying App in an AppView object changes state.
  // *
  // * @param view
  // * The AppView in which the App was changed.
  // * @param app
  // * The App that was changed.
  // */
  // public void onAppChanged(SnippetView view, App app);
  // }
  //
  // @Override
  // public void onRatingChanged(RatingBar ratingBar, float rating,
  // boolean fromUser)
  // {
  // m_app.setRating(rating);
  // notifyOnAppChangeListener();
  // }
}