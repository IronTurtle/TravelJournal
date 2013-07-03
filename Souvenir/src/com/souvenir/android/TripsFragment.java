package com.souvenir.android;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.evernote.client.android.InvalidAuthenticationException;
import com.evernote.edam.type.Note;
import com.souvenir.android.CreateDialogFragment.DialogListener;
import com.souvenir.android.NewTripDialogFragment.TripDialogListener;
import com.souvenir.android.database.SouvenirContentProvider;
import com.souvenir.android.database.SouvenirContract;

public class TripsFragment extends ParentFragment implements OnClickListener,
    LoaderManager.LoaderCallbacks<Cursor>, DialogListener, TripDialogListener
{
  private final static String CREATE_TAG = "CREATE";

  SnippetCursorAdapter adapter;
  ListView listView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    ActionBar abs = getSherlockActivity().getSupportActionBar();
    abs.setDisplayHomeAsUpEnabled(true);
    setHasOptionsMenu(true);
    View view = inflater.inflate(R.layout.fragment_trips, container, false);
    Cursor resCursor;
    if ((resCursor = getActivity().getContentResolver().query(
        Uri.parse(SouvenirContentProvider.CONTENT_URI
            + SouvenirContentProvider.DatabaseConstants.TRIP), null, null,
        null, null)) != null
        && resCursor.getCount() > 0)
    {
      while (resCursor.moveToNext())
      {
        System.out.println(new STrip(resCursor).tripName);
      }
    }
    resCursor.close();

    listView = (ListView) view.findViewById(R.id.lview);
    adapter = new SnippetCursorAdapter(getActivity(), null, 0);

    listView.setAdapter(adapter);
    listView.setScrollingCacheEnabled(false);
    listView.setOnItemClickListener(new OnItemClickListener()
    {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id)
      {
        Intent intent = new Intent(TripsFragment.this.getActivity(),
            NoteActivity.class).putExtra("note", ((SnippetView) view).getNote());
        getActivity().startActivityForResult(intent, 300);
      }
    });
    getActivity().getSupportLoaderManager().initLoader(1, null, this);

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
  {
    inflater.inflate(R.menu.trips_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
    case android.R.id.home:
      // This ID represents the Home or Up button. In the case of this
      // activity, the Up button is shown. Use NavUtils to allow users
      // to navigate up one level in the application structure. For
      // more details, see the Navigation pattern on Android Design:
      //
      // http://developer.android.com/design/patterns/navigation.html#up-vs-back
      //
      // syncCheck();
      break;

    case R.id.menu_add_note:
      // startActivity(new Intent(getActivity(), NoteActivity.class));
      CreateDialogFragment dialogFragment = CreateDialogFragment
          .newInstance(TripsFragment.this);
      dialogFragment.show(getFragmentManager(), CREATE_TAG);
      break;
    case R.id.menu_refresh:
      getActivity().startService(
          new Intent(getActivity(), EvernoteSyncService.class));
      getActivity().getSupportLoaderManager().restartLoader(1, null, this);
      adapter.notifyDataSetChanged();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onResume()
  {
    super.onResume();
    updateUi();
    getActivity().getSupportLoaderManager().restartLoader(1, null, this);
    adapter.notifyDataSetChanged();
  }

  private void updateUi()
  {
    if (mEvernoteSession.isLoggedIn())
    {
      // mBtnAuth.setText(R.string.label_log_out);
      // View b = this.getView().findViewById(R.id.auth_button);
      // b.setVisibility(View.GONE);

      // checkForTravelNotebook();
      // syncCheck();
    }
    else
    {
      // View b = this.getView().findViewById(R.id.auth_button);
      // b.setVisibility(View.VISIBLE);
      // mBtnAuth.setText(R.string.label_log_in);
    }
  }

  public void startAuth(View view)
  {
    if (mEvernoteSession.isLoggedIn())
    {
      try
      {
        mEvernoteSession.logOut(this.getActivity().getApplicationContext());
      }
      catch (InvalidAuthenticationException e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      mEvernoteSession.authenticate(this.getActivity());
    }
    updateUi();
  }

  public void addNoteOnClick(View view)
  {

    Intent intent = new Intent(this.getActivity().getApplicationContext(),
        NoteFragment.class);

    this.startActivityForResult(intent, 200);

  }

  public void goToItineraryOnClick(View view)
  {
    Intent intent = new Intent(this.getActivity().getApplicationContext(),
        ItineraryActivity.class);

    this.startActivityForResult(intent, 100);
  }

  protected void onPostExecute(Note note)
  {
    this.getActivity();

    if (note == null)
    {
      Toast.makeText(this.getActivity().getApplicationContext(),
          R.string.err_creating_note, Toast.LENGTH_LONG).show();
      return;
    }

    Toast.makeText(this.getActivity().getApplicationContext(),
        R.string.success_creating_note, Toast.LENGTH_LONG).show();

  }

  @Override
  public void onClick(View v)
  {
    // TODO Auto-generated method stub

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
      ((SnippetView) view).setSNote(note);
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
      SnippetView sv = new SnippetView(context, note);
      return sv;
    }
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    String[] projection = { SouvenirContract.SouvenirNote._ID,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_LOCATION,
        SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID };

    Uri uri = Uri.parse(SouvenirContentProvider.CONTENT_URI
        + SouvenirContentProvider.DatabaseConstants.NOTE);

    CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, null,
        null, null, SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_MODIFY_DATE
            + " DESC");
    return cursorLoader;
    // return null;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
  {
    this.adapter.swapCursor(cursor);
    // this.m_appAdapter.setOnAppChangeListener(this);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader)
  {
    this.adapter.swapCursor(null);
  }

  // public static class CreateDialogFragment extends DialogFragment
  // {
  // @Override
  // public Dialog onCreateDialog(Bundle savedInstanceState)
  // {
  // // Use the Builder class for convenient dialog construction
  // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
  // builder
  // .setMessage(R.string.create_dialog)
  // .setPositiveButton(R.string.create_dialog_souvenir,
  // new DialogInterface.OnClickListener()
  // {
  // public void onClick(DialogInterface dialog, int id)
  // {
  // // Create Souvenir
  //
  // startActivity(new Intent(getActivity(), NoteActivity.class));
  // }
  // })
  // .setNegativeButton(R.string.create_dialog_trip,
  // new DialogInterface.OnClickListener()
  // {
  // public void onClick(DialogInterface dialog, int id)
  // {
  // // Create another dialog to create Trip name, Location, & date
  // // range
  // // Trip name is the only mandatory field
  // Toast.makeText(getActivity().getApplicationContext(),
  // "Creating Trip", Toast.LENGTH_SHORT).show();
  //
  // }
  // });
  // // Create the AlertDialog object and return it
  // return builder.create();
  // }
  //
  // }

  public void openTripDialog()
  {

    NewTripDialogFragment dialogFragment = NewTripDialogFragment
        .newInstance(TripsFragment.this);
    dialogFragment.show(getFragmentManager(), CREATE_TAG);
  }

  @Override
  public void onDialogPositiveClick(DialogFragment dialog)
  {
    startActivity(new Intent(getActivity(), NoteActivity.class));
  }

  @Override
  public void onDialogNegativeClick(DialogFragment dialog)
  {
    // Create another dialog to create Trip name, Location, & date
    // range
    // Trip name is the only mandatory field
    Toast.makeText(getActivity().getApplicationContext(), "Creating Trip",
        Toast.LENGTH_SHORT).show();
    openTripDialog();
  }

  @Override
  public void onTripDialogPositiveClick(DialogFragment dialog, Bundle tripInfo)
  {
    Toast.makeText(getActivity().getApplicationContext(), "Trip Created",
        Toast.LENGTH_SHORT).show();

    // System.out.println("name: " + tripInfo.getCharSequence("tripName") + "\n"
    // + "genLocation: " + tripInfo.getCharSequence("tripGenLoc") + "\n"
    // + "startDate: " + tripInfo.getCharSequence("tripStartDate") + "\n"
    // + "endDate: " + tripInfo.getCharSequence("tripEndDate"));
    STrip sTrip = new STrip(tripInfo.getCharSequence("tripName").toString(),
        tripInfo.getCharSequence("tripGenLoc").toString(), tripInfo
            .getCharSequence("tripStartDate").toString(), tripInfo
            .getCharSequence("tripEndDate").toString());
    Uri uri = getActivity().getContentResolver().insert(
        Uri.parse(SouvenirContentProvider.CONTENT_URI
            + SouvenirContentProvider.DatabaseConstants.TRIP),
        sTrip.toContentValues());
    int id = Integer.valueOf(uri.getLastPathSegment());
    sTrip.setId(id);
    System.out.println("Trip ID: " + sTrip.getId());
  }

  @Override
  public void onTripDialogNegativeClick(DialogFragment dialog)
  {

    Toast.makeText(getActivity().getApplicationContext(), "New Trip Cancelled",
        Toast.LENGTH_SHORT).show();
  }

}
