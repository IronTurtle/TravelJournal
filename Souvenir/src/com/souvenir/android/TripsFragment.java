package com.souvenir.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.souvenir.android.database.SouvenirContentProvider;

public class TripsFragment extends ParentFragment implements OnClickListener,
    LoaderManager.LoaderCallbacks<Cursor>
{
  private final static String CREATE_TAG = "CREATE";

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
      CreateDialogFragment cdf = new CreateDialogFragment();
      cdf.show(getFragmentManager(), CREATE_TAG);
      break;

    }
    return super.onOptionsItemSelected(item);
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

  public static class CreateDialogFragment extends DialogFragment
  {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      CharSequence[] items = { "Trip", "Souvenir" };
      // Use the Builder class for convenient dialog construction
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder
          .setMessage(R.string.create_dialog)
          .setPositiveButton(R.string.create_dialog_souvenir,
              new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface dialog, int id)
                {
                  // Create Souvenir
                  startActivity(new Intent(getActivity(), NoteActivity.class));
                }
              })
          .setNegativeButton(R.string.create_dialog_trip,
              new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface dialog, int id)
                {
                  // Create another dialog to create Trip name, Location, & date
                  // range
                  // Trip name is the only mandatory field
                  Toast.makeText(getActivity().getApplicationContext(),
                      "Creating Trip", Toast.LENGTH_SHORT).show();

                }
              });
      // Create the AlertDialog object and return it
      return builder.create();
    }
  }

}
