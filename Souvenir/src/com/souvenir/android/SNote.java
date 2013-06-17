package com.souvenir.android;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.evernote.edam.type.LazyMap;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteAttributes;
import com.evernote.edam.type.Resource;
import com.souvenir.database.SouvenirContract;

public class SNote implements Parcelable
{
  public static final Parcelable.Creator<SNote> CREATOR = new Parcelable.Creator<SNote>()
  {
    public SNote createFromParcel(Parcel in)
    {
      return new SNote(in);
    }

    public SNote[] newArray(int size)
    {
      return new SNote[size];
    }
  };
  String content = null;
  long createDate = -1;
  boolean dirty = false;
  String evernoteGUID = null;
  int id = -1;
  String location = null;
  long modifyDate = -1;
  List<Resource> resources = null;
  int syncNum = -1;
  ArrayList<String> tags = null;
  String title = null;
  String tripID = null;

  String trophyNumber = null;

  public SNote(Cursor cursor)
  {
    this.id = cursor.getInt(cursor
        .getColumnIndexOrThrow(SouvenirContract.SouvenirNote._ID));
    this.title = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE));
    this.content = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT));
    this.location = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_LOCATION));
    this.evernoteGUID = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID));
    this.dirty = cursor
        .getInt(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_DIRTY)) == 1;
    this.createDate = cursor
        .getLong(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CREATE_DATE));
    this.modifyDate = cursor
        .getLong(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_MODIFY_DATE));
    // this.content = ""
    // + cursor
    // .getLong(cursor
    // .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_MODIFY_DATE));
  }

  public SNote(Note note)
  {
    this.title = note.getTitle();
    this.content = note.getContent();
    // this.location = note.getAttributes().ge;
    this.modifyDate = note.getUpdated();
    this.createDate = note.getCreated();
    this.evernoteGUID = note.getGuid();
    this.resources = note.getResources();
    // this.tags = null;
    // this.trophyNumber = trophyNumber;
    // this.tripID = tripID;
  }

  @SuppressWarnings("unchecked")
  public SNote(Parcel in)
  {
    super();
    this.id = in.readInt();
    this.title = in.readString();
    this.content = in.readString();
    this.location = in.readString();
    this.modifyDate = in.readLong();
    this.createDate = in.readLong();
    this.evernoteGUID = in.readString();
    in.readList(this.resources, Resource.class.getClassLoader());
    this.tags = in.readArrayList(String.class.getClassLoader());
    this.trophyNumber = in.readString();
    this.tripID = in.readString();
  }

  public SNote(String title, String content, String location)
  {
    super();
    this.title = title;
    this.content = content;
    this.location = location;
  }

  public SNote(String title, String content, String location,
      String evernoteGUID, String trophyNumber, String tripID)
  {
    super();
    this.title = title;
    this.content = content;
    this.location = location;
    this.evernoteGUID = evernoteGUID;
    this.trophyNumber = trophyNumber;
    this.tripID = tripID;
  }

  public SNote(String title, String content, String location, long modifyDate,
      long createDate, String evernoteGUID, ArrayList<Resource> resources,
      ArrayList<String> tags, String trophyNumber, String tripID)
  {
    super();
    this.title = title;
    this.content = content;
    this.location = location;
    this.modifyDate = modifyDate;
    this.createDate = createDate;
    this.evernoteGUID = evernoteGUID;
    this.resources = resources;
    this.tags = tags;
    this.trophyNumber = trophyNumber;
    this.tripID = tripID;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  public String getContent()
  {
    return content;
  }

  public long getCreateDate()
  {
    return createDate;
  }

  public String getEvernoteGUID()
  {
    return evernoteGUID;
  }

  public int getId()
  {
    return id;
  }

  public String getLocation()
  {
    return location;
  }

  public long getModifyDate()
  {
    return modifyDate;
  }

  public List<Resource> getResource()
  {
    return resources;
  }

  public int getSyncNum()
  {
    return syncNum;
  }

  public ArrayList<String> getTags()
  {
    return tags;
  }

  public String getTitle()
  {
    return title;
  }

  public String getTripID()
  {
    return tripID;
  }

  public String getTrophyNumber()
  {
    return trophyNumber;
  }

  public boolean isDirty()
  {
    return dirty;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public void setCreateDate(long createDate)
  {
    this.createDate = createDate;
  }

  public void setDirty(boolean dirty)
  {
    this.dirty = dirty;
  }

  public void setEvernoteGUID(String evernoteGUID)
  {
    this.evernoteGUID = evernoteGUID;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public void setLocation(String location)
  {
    this.location = location;
  }

  public void setModifyDate(long modifyDate)
  {
    this.modifyDate = modifyDate;
  }

  public void setResource(ArrayList<Resource> resources)
  {
    this.resources = resources;
  }

  public void setSyncNum(int syncNum)
  {
    this.syncNum = syncNum;
  }

  public void setTags(ArrayList<String> tags)
  {
    this.tags = tags;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public void setTripID(String tripID)
  {
    this.tripID = tripID;
  }

  public void setTrophyNumber(String trophyNumber)
  {
    this.trophyNumber = trophyNumber;
  }

  public ContentValues toContentValues()
  {
    ContentValues values = new ContentValues();

    values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TITLE, title);
    values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CONTENT, content);
    values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_LOCATION,
        location);
    values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_DIRTY,
        dirty == true ? 1 : 0);
    values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_GUID,
        evernoteGUID);
    values
        .put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_SYNC_NUM, syncNum);
    values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_CREATE_DATE,
        createDate);
    values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_MODIFY_DATE,
        modifyDate);
    return values;
  }

  public Note toNote()
  {
    Note note = new Note();
    // if (NOTEBOOK_GUID != null)
    // {
    // note.setNotebookGuid(NOTEBOOK_GUID);
    // }
    if (this.evernoteGUID != null)
      note.setGuid(this.evernoteGUID);
    note.setTitle(this.title);
    note.setContent(this.content);
    note.setCreated(this.createDate);
    note.setUpdated(this.modifyDate);
    NoteAttributes attr = new NoteAttributes();
    LazyMap map = new LazyMap();

    map.putToFullMap("LOCATION", location);
    // if (longitude != 0 && latitude != 0)
    // {
    // attr.setLongitude(longitude);
    // attr.setLatitude(latitude);
    // }
    // else
    // {
    // attr.setLatitudeIsSet(false);
    // attr.setLongitudeIsSet(false);
    // if (!selectedPlace)
    // {
    // attr.setPlaceName(LOCATION_NOT_SPECIFIED);
    // }
    // }
    //
    // if (selectedPlace)
    // {
    // attr.setPlaceName(location);
    // }
    attr.setSourceApplication("Souvenir App (Android)");
    attr.setContentClass("com.souvenir.android");
    note.setAttributes(attr);
    return note;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeInt(id);
    dest.writeString(this.title);
    dest.writeString(this.content);
    dest.writeString(this.location);
    dest.writeLong(this.modifyDate);
    dest.writeLong(this.createDate);
    dest.writeString(this.evernoteGUID);
    dest.writeList(this.resources);
    dest.writeList(this.tags);
    dest.writeString(this.trophyNumber);
    dest.writeString(this.tripID);
  }
}
