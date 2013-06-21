package com.souvenir.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import com.evernote.client.android.EvernoteUtil;
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
    this.hash = EvernoteUtil.bytesToHex(hash);
    this.mime = mime;
    this.path = path;
  }

  public SResource(Cursor cursor)
  {
    this.caption = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_CAPTION));
    this.evernoteGUID = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_GUID));
    this.hash = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_HASH));
    this.location = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_LOCATION));
    this.mime = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_MIME));
    this.noteId = cursor
        .getInt(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_NOTE_ID));
    this.path = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirResource.COLUMN_NAME_RESOURCE_PATH));
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
    this.hash = EvernoteUtil.bytesToHex(resource.getData().getBodyHash());
    // this.location = resource.getAttributes();
    this.mime = resource.getMime();
    // this.noteId = in.readInt();

    File mediaFile = null;
    File mediaStorageDir = new File(
        Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "Souvenir");
    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs())
    {
    }
    String timestamp = DateFormat.getDateTimeInstance().format(
        new Date(System.currentTimeMillis()));

    mediaFile = new File(mediaStorageDir.getPath() + File.separator
        + System.currentTimeMillis());

    FileOutputStream fos = null;
    try
    {
      fos = new FileOutputStream(mediaFile.getAbsoluteFile());
    }
    catch (FileNotFoundException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    try
    {
      fos.write(resource.getData().getBody());
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try
    {
      fos.close();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.path = mediaFile.getAbsolutePath();

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
