package com.souvenir.android;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.evernote.edam.type.Resource;
import com.souvenir.database.SouvenirContract;

public class SResource implements Parcelable
{
  public static final Parcelable.Creator<SResource> CREATOR = new Parcelable.Creator<SResource>()
  {
    public SResource createFromParcel(Parcel in)
    {
      return new SResource(in);
    }

    public SResource[] newArray(int size)
    {
      return new SResource[size];
    }
  };

  String caption;
  String evernoteGUID;
  String hash;
  String location;
  String mime;
  int noteId;
  String path;

  public SResource(String caption, String hash, String mime, String path)
  {
    super();
    this.caption = caption;
    this.hash = hash;
    this.mime = mime;
    this.path = path;
  }

  public SResource(String caption, byte[] hash, String mime, String path)
  {
    super();
    this.caption = caption;
    this.hash = bytesToHex(hash);
    this.mime = mime;
    this.path = path;
  }

  public SResource(Cursor cursor)
  {

  }

  public SResource(Parcel in)
  {
    super();
    this.caption = in.readString();
    this.evernoteGUID = in.readString();
    this.hash = in.readString();
    this.location = in.readString();
    this.mime = in.readString();
    this.noteId = in.readInt();
    this.path = in.readString();
  }

  public SResource(Resource resource, String caption)
  {
    this.caption = caption;
    this.evernoteGUID = resource.getGuid();
    this.hash = bytesToHex(resource.getData().getBodyHash());
    // this.location = resource.getAttributes();
    this.mime = resource.getMime();
    // this.noteId = in.readInt();
    // this.path = in.readString();
  }

  // Evernote java sdk
  public static String bytesToHex(byte[] bytes)
  {
    StringBuilder sb = new StringBuilder();
    for (byte hashByte : bytes)
    {
      int intVal = 0xff & hashByte;
      if (intVal < 0x10)
      {
        sb.append('0');
      }
      sb.append(Integer.toHexString(intVal));
    }
    return sb.toString();
  }

  @Override
  public int describeContents()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeString(this.caption);
    dest.writeString(this.evernoteGUID);
    dest.writeString(this.hash);
    dest.writeString(this.location);
    dest.writeString(this.mime);
    dest.writeInt(this.noteId);
    dest.writeString(this.path);
  }

  public String getCaption()
  {
    return caption;
  }

  public void setCaption(String caption)
  {
    this.caption = caption;
  }

  public String getHash()
  {
    return hash;
  }

  public void setHash(String hash)
  {
    this.hash = hash;
  }

  public String getMime()
  {
    return mime;
  }

  public void setMime(String mime)
  {
    this.mime = mime;
  }

  public int getNoteId()
  {
    return noteId;
  }

  public void setNoteId(int noteId)
  {
    this.noteId = noteId;
  }

  public String getPath()
  {
    return path;
  }

  public void setPath(String path)
  {
    this.path = path;
  }

  public String getEvernoteGUID()
  {
    return evernoteGUID;
  }

  public void setEvernoteGUID(String evernoteGUID)
  {
    this.evernoteGUID = evernoteGUID;
  }

  public String getLocation()
  {
    return location;
  }

  public void setLocation(String location)
  {
    this.location = location;
  }

  public static Parcelable.Creator<SResource> getCreator()
  {
    return CREATOR;
  }

  public ContentValues toContentValues()
  {
    ContentValues values = new ContentValues();

    values.put(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_CAPTION,
        this.caption);
    values.put(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_GUID,
        this.getEvernoteGUID());
    values.put(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_HASH,
        this.getHash());
    values.put(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_LOCATION,
        this.location);
    values.put(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_MIME,
        this.mime);
    values.put(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_NOTE_ID,
        this.noteId);
    values.put(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_PATH,
        this.path);

    return values;
  }
}
