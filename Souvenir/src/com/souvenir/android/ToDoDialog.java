package com.souvenir.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.souvenir.android.database.SouvenirContentProvider;
import com.souvenir.android.database.SouvenirContract;

public class ToDoDialog extends DialogFragment implements
    OnClickListener, LoaderManager.LoaderCallbacks<Cursor>
{
  private static final String ARG_LISTENER_TYPE = "listenerType";
  private TripDialogListener mListener;

  ListView listView;
  SnippetCursorAdapter adapter;

  static enum ListenerType
  {
    ACTIVITY, FRAGMENT
  }

  public interface TripDialogListener
  {
    public void onTripDialogPositiveClick(DialogFragment dialog, Bundle tripInfo);

    public void onTripDialogNegativeClick(DialogFragment dialog);
  }

  public static NewTripDialogFragment newInstance(TripDialogListener listener)
  {
    final NewTripDialogFragment instance;

    if (listener instanceof Activity)
    {
      instance = createInstance(ListenerType.ACTIVITY);
    }
    else if (listener instanceof Fragment)
    {
      instance = createInstance(ListenerType.FRAGMENT);
      instance.setTargetFragment((Fragment) listener, 0);
    }
    else
    {
      throw new IllegalArgumentException(listener.getClass()
          + " must be either an Activity or a Fragment");
    }

    return instance;
  }

  private static NewTripDialogFragment createInstance(ListenerType type)
  {
    NewTripDialogFragment fragment = new NewTripDialogFragment();

    Bundle args = new Bundle();
    args.putSerializable(ARG_LISTENER_TYPE, type);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    // TODO: Create your dialog here

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    // builder.setMessage(R.string.trip_dialog_title);

    // setup view
    Context context = this.getActivity().getApplicationContext();
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    builder.setView(inflater.inflate(R.layout.dialog_todo_list, null));

    listView = (ListView) getView().findViewById(R.id.todo_lview);
    adapter = new SnippetCursorAdapter(getActivity(), null, 0);

    listView.setAdapter(adapter);
    listView.setScrollingCacheEnabled(false);
    listView.setOnItemClickListener(new OnItemClickListener()
    {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id)
      {

        // Intent intent = new Intent(ToDoDialog.this.getActivity(),
        // NoteActivity.class).putExtra("note",
        // ((SouvenirSnippetView) view).getNote());
        // getActivity().startActivityForResult(intent, 300);
      }
    });
    getActivity().getSupportLoaderManager().initLoader(2, null, this);

    return builder.create();
  }

  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);
    mListener = (TripDialogListener) getTargetFragment();
    // // Find out how to get the DialogListener instance to send the callback
    // // events to
    // Bundle args = getArguments();
    // ListenerType listenerType = (ListenerType) args
    // .getSerializable(ARG_LISTENER_TYPE);
    //
    // switch (listenerType)
    // {
    // case ACTIVITY:
    // {
    // // Send callback events to the hosting activity
    // mListener = (TripDialogListener) activity;
    // break;
    // }
    // case FRAGMENT:
    // {
    // // Send callback events to the "target" fragment
    // mListener = (TripDialogListener) getTargetFragment();
    // break;
    // }
    // }
  }

  @Override
  public void onDetach()
  {
    super.onDetach();
    mListener = null;
  }

  public class SnippetCursorAdapter extends CursorAdapter
  {

    /**
     * The OnAppChangeListener that should be connected to each of the AppViews
     * created/managed by this Adapter.
     */
    // private OnAppChangeListener m_listener;

    public SnippetCursorAdapter(Context context, Cursor cursor, int flags)
    {
      super(context, cursor, flags);
      // this.m_listener = null;
    }

    //
    // /**
    // * Mutator method for changing the OnAppChangeListener.
    // *
    // * @param listener
    // * The OnAppChangeListener that will be notified when the
    // * internal state of any Joke contained in one of this Adapters
    // * AppViews is changed.
    // */
    // public void setOnAppChangeListener(OnAppChangeListener mListener)
    // {
    // // this.m_listener = mListener;
    // }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
      SNote note = new SNote(cursor);
      String[] args = { "" + note.getId() };
      Cursor resCursor;
      if ((resCursor = getActivity().getContentResolver().query(
          Uri.parse(SouvenirContentProvider.CONTENT_URI
              + SouvenirContentProvider.DatabaseConstants.NOTE_RES),
          null,
          SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_NOTE_ID + "="
              + note.getId(), null, null)) != null
          && resCursor.getCount() > 0)
      {
        while (resCursor.moveToNext())
        {
          note.addResource(new SResource(resCursor));
        }
      }
      resCursor.close();
      // ((AppView) view).setOnAppChangeListener(null);
      ((SouvenirSnippetView) view).setSNote(note);
      // ((AppView) view).setOnAppChangeListener(this.m_listener);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
      SNote note = new SNote(cursor);
      String[] args = { "" + note.getId() };
      Cursor resCursor;
      if ((resCursor = getActivity().getContentResolver().query(
          Uri.parse(SouvenirContentProvider.CONTENT_URI
              + SouvenirContentProvider.DatabaseConstants.NOTE_RES), null,
          null, null, null)) != null
          && resCursor.getCount() > 0)
      {
        while (resCursor.moveToNext())
        {
          SResource sc = new SResource(resCursor);
          // System.out.println(note.getId() + " " + sc.getNoteId());
          note.addResource(sc);
        }
      }
      resCursor.close();
      SouvenirSnippetView sv = new SouvenirSnippetView(context, note);
      return sv;
    }
  }

  @Override
  public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onLoaderReset(Loader<Cursor> arg0)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onClick(View v)
  {
    // TODO Auto-generated method stub

  }

}
