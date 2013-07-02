package com.souvenir.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

public class CreateDialogFragment extends DialogFragment
{

  private static final String ARG_LISTENER_TYPE = "listenerType";
  private DialogListener mListener;

  static enum ListenerType
  {
    ACTIVITY, FRAGMENT
  }

  public interface DialogListener
  {
    public void onDialogPositiveClick(DialogFragment dialog);

    public void onDialogNegativeClick(DialogFragment dialog);
  }

  public static CreateDialogFragment newInstance(DialogListener listener)
  {
    final CreateDialogFragment instance;

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

  private static CreateDialogFragment createInstance(ListenerType type)
  {
    CreateDialogFragment fragment = new CreateDialogFragment();

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
    builder.setMessage(R.string.create_dialog);
    builder.setPositiveButton(R.string.create_dialog_souvenir,
        new OnClickListener()
        {
          public void onClick(DialogInterface dialog, int which)
          {
            mListener.onDialogPositiveClick(CreateDialogFragment.this);
          }
        });
    builder.setNegativeButton(R.string.create_dialog_trip,
        new OnClickListener()
        {
          public void onClick(DialogInterface dialog, int which)
          {
            mListener.onDialogNegativeClick(CreateDialogFragment.this);
          }
        });

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
      mListener = (DialogListener) activity;
      break;
    }
    case FRAGMENT:
    {
      // Send callback events to the "target" fragment
      mListener = (DialogListener) getTargetFragment();
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