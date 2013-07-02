package com.souvenir.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

public class NewTripDialogFragment extends DialogFragment
{

  private static final String ARG_LISTENER_TYPE = "listenerType";
  private TripDialogListener mListener;

  static enum ListenerType
  {
    ACTIVITY, FRAGMENT
  }

  public interface TripDialogListener
  {
    public void onTripDialogPositiveClick(DialogFragment dialog);

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
    builder.setPositiveButton(android.R.string.ok, new OnClickListener()
    {
      public void onClick(DialogInterface dialog, int which)
      {
        mListener.onTripDialogPositiveClick(NewTripDialogFragment.this);
      }
    });
    builder.setNegativeButton(android.R.string.cancel, new OnClickListener()
    {
      public void onClick(DialogInterface dialog, int which)
      {
        mListener.onTripDialogNegativeClick(NewTripDialogFragment.this);
      }
    });

    // setup view
    Context context = this.getActivity().getApplicationContext();
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    builder.setView(inflater.inflate(R.layout.trip_dialog, null));

    return builder.create();
  }

  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);

    // Find out how to get the DialogListener instance to send the callback
    // events to
    Bundle args = getArguments();
    ListenerType listenerType = (ListenerType) args
        .getSerializable(ARG_LISTENER_TYPE);

    switch (listenerType)
    {
    case ACTIVITY:
    {
      // Send callback events to the hosting activity
      mListener = (TripDialogListener) activity;
      break;
    }
    case FRAGMENT:
    {
      // Send callback events to the "target" fragment
      mListener = (TripDialogListener) getTargetFragment();
      break;
    }
    }
  }

  @Override
  public void onDetach()
  {
    super.onDetach();
    mListener = null;
  }
}