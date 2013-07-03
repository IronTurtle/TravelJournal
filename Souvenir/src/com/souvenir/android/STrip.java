package com.souvenir.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.evernote.edam.type.Tag;
import com.souvenir.android.database.SouvenirContentProvider;
import com.souvenir.android.database.SouvenirContract;

public class STrip implements Parcelable
{
  public static final Parcelable.Creator<STrip> CREATOR = new Parcelable.Creator<STrip>()
  {
    public STrip createFromParcel(Parcel in)
    {
      return new STrip(in);
    }

    public STrip[] newArray(int size)
    {
      return new STrip[size];
    }
  };

  int id = -1;
  int syncNum = -1;
  String evernoteGUID;
  // String parentEvernoteGUID;
  String tripName;
  String generalLocation;
  String startDate;
  String endDate;
  boolean dirty = false;

  public STrip(Cursor cursor)
  {
    this.id = cursor.getInt(cursor
        .getColumnIndexOrThrow(SouvenirContract.SouvenirTrip._ID));
    this.tripName = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_NAME));
    this.evernoteGUID = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_GUID));
    this.dirty = cursor
        .getInt(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_DIRTY)) == 1;
    this.syncNum = cursor
        .getInt(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_SYNC_NUM));
  }

  public STrip(Tag tag)
  {
    this.tripName = tag.getName();
    this.evernoteGUID = tag.getGuid();
    this.syncNum = tag.getUpdateSequenceNum();
  }

  public STrip(Parcel in)
  {
    super();
    this.id = in.readInt();
    this.tripName = in.readString();
    this.evernoteGUID = in.readString();
    this.dirty = in.readInt() == 1;
    this.syncNum = in.readInt();
  }

  // trip name-only
  public STrip(String tripName)
  {
    super();
    this.tripName = tripName;
  }

  // trip name & general location
  public STrip(String tripName, String generalLocation)
  {
    super();
    this.tripName = tripName;
    this.generalLocation = generalLocation;
  }

  // trip name, general location, start & end date
  public STrip(String tripName, String generalLocation, String startDate,
      String endDate)
  {
    super();
    this.tripName = tripName;
    this.generalLocation = generalLocation;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  public ContentValues toContentValues()
  {
    ContentValues values = new ContentValues();

    values.put(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_NAME, tripName);
    values.put(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_GENLOCATION,
        generalLocation);
    values.put(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_STARTDATE,
        startDate);
    values.put(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_ENDDATE, endDate);
    values.put(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_GUID,
        evernoteGUID);
    values
        .put(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_SYNC_NUM, syncNum);
    values.put(SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_GUID, dirty ? 1
        : 0);
    return values;
  }

  public Tag toTag()
  {
    Tag tag = new Tag();
    tag.setName(tripName);
    return tag;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeInt(this.id);
    dest.writeString(this.tripName);
    dest.writeString(this.generalLocation);
    dest.writeString(this.startDate);
    dest.writeString(this.endDate);
    dest.writeString(this.evernoteGUID);
    dest.writeInt(this.dirty ? 1 : 0);
    dest.writeInt(this.syncNum);
  }

  public void insert(Context applicationContext)
  {
    Cursor cursor;
    String[] args = { evernoteGUID };
    if ((cursor = applicationContext.getContentResolver().query(
        Uri.parse(SouvenirContentProvider.CONTENT_URI
            + SouvenirContentProvider.DatabaseConstants.TRIP), null,
        SouvenirContract.SouvenirTrip.COLUMN_NAME_TRIP_GUID + "=?", args, null)) != null
        && cursor.getCount() > 0)
    {
      // System.out.println("This GUID already exists "
      // + cursor.getCount());
      // System.out.println("old note");
      while (cursor.moveToNext())
      {
        applicationContext.getContentResolver().update(
            Uri.parse(SouvenirContentProvider.CONTENT_URI
                + SouvenirContentProvider.DatabaseConstants.TRIP),
            toContentValues(), null, null);
        // System.out.println(oldNote.getEvernoteGUID());
      }
      // System.out.println("syncnumber: " + syncnum);
    }
    else
    {
      Uri uri = applicationContext.getContentResolver().insert(
          Uri.parse(SouvenirContentProvider.CONTENT_URI
              + SouvenirContentProvider.DatabaseConstants.TRIP),
          toContentValues());
    }
    cursor.close();

    // int id = Integer.valueOf(uri.getLastPathSegment());
    // setId(id);
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public int getSyncNum()
  {
    return syncNum;
  }

  public void setSyncNum(int syncNum)
  {
    this.syncNum = syncNum;
  }

  public String getEvernoteGUID()
  {
    return evernoteGUID;
  }

  public void setEvernoteGUID(String evernoteGUID)
  {
    this.evernoteGUID = evernoteGUID;
  }

  // public String getParentEvernoteGUID()
  // {
  // return parentEvernoteGUID;
  // }
  //
  // public void setParentEvernoteGUID(String parentEvernoteGUID)
  // {
  // this.parentEvernoteGUID = parentEvernoteGUID;
  // }

  public String getTripName()
  {
    return tripName;
  }

  public void setTripName(String tripName)
  {
    this.tripName = tripName;
  }

  public String getGeneralLocation()
  {
    return generalLocation;
  }

  public void setGeneralLocation(String generalLocation)
  {
    this.generalLocation = generalLocation;
  }

  public String getStartDate()
  {
    return startDate;
  }

  public void setStartDate(String startDate)
  {
    this.startDate = startDate;
  }

  public String getEndDate()
  {
    return endDate;
  }

  public void setEndDate(String endDate)
  {
    this.endDate = endDate;
  }

  public boolean isDirty()
  {
    return dirty;
  }

  public void setDirty(boolean dirty)
  {
    this.dirty = dirty;
  }
}
