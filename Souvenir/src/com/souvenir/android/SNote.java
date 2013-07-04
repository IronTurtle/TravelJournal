package com.souvenir.android;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.type.LazyMap;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteAttributes;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.souvenir.android.database.SouvenirContract;

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
  String content = null;// 0
  long createDate = -1;// 1
  boolean dirty = false;
  String evernoteGUID = null;
  int id = -1;
  String location = null;// 2
  long modifyDate = -1;// 3
  HashMap<String, SResource> r = new HashMap<String, SResource>();
  Bundle bundle = new Bundle();
  List<SResource> resources = new ArrayList<SResource>();// 4
  int syncNum = -1;
  ArrayList<String> tags = null;// 5
  String title = null;// 6
  String tripID = null;//
  String trophyNumber = null;//
  Boolean[] issetVector = new Boolean[7];
  EnumSet<isset> issetV = EnumSet.noneOf(isset.class);
  boolean finished = false; //

  public enum isset
  {
    content, location, resources, tags, title, tripid // tripid just added (7/4
                                                      // - Kevin)
  }

  // From Adamski's answer
  public static int encode(EnumSet<isset> set)
  {
    int ret = 0;

    for (isset val : set)
    {
      ret |= 1 << val.ordinal();
    }

    return ret;
  }

  @SuppressWarnings("unchecked")
  public static EnumSet<isset> decode(int code, Class enumType)
  {
    try
    {
      isset[] values = (isset[]) enumType.getMethod("values").invoke(null);
      EnumSet<isset> result = EnumSet.noneOf(enumType);
      while (code != 0)
      {
        int ordinal = Integer.numberOfTrailingZeros(code);
        code ^= Integer.lowestOneBit(code);
        result.add(values[ordinal]);
      }
      return result;
    }
    catch (IllegalAccessException ex)
    {
      // Shouldn't happen
      throw new RuntimeException(ex);
    }
    catch (InvocationTargetException ex)
    {
      // Probably a NullPointerException, caused by calling this method
      // from within E's initializer.
      throw (RuntimeException) ex.getCause();
    }
    catch (NoSuchMethodException ex)
    {
      // Shouldn't happen
      throw new RuntimeException(ex);
    }
  }

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
    this.issetV = decode(
        cursor
            .getInt(cursor
                .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_ISSET)),
        isset.class);
    this.syncNum = cursor
        .getInt(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_SYNC_NUM));
    this.finished = cursor
        .getInt(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_FINISHED)) == 1;

    this.tripID = cursor
        .getString(cursor
            .getColumnIndexOrThrow(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TRIP_ID));

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
    this.syncNum = note.getUpdateSequenceNum();
    // this.resources = note.getSResources();
    // this.tags = null;
    // this.trophyNumber = trophyNumber;
    // this.tripID = tripID;
    this.finished = false;
    if (note.isSetTagGuids())
      this.tripID = note.getTagGuids().get(0);
    else
      this.tripID = "uncategorized";

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
    in.readList(this.resources, SResource.class.getClassLoader());
    this.tags = in.readArrayList(String.class.getClassLoader());
    this.trophyNumber = in.readString();
    this.tripID = in.readString();
    this.bundle = in.readBundle(SResource.class.getClassLoader());
    this.syncNum = in.readInt();
    this.r = in.readHashMap(SResource.class.getClassLoader());
    this.finished = (in.readInt() == 1);
  }

  public SNote(String title, String content, String location)
  {
    super();
    this.title = title;
    this.content = content;
    this.location = location;
    this.createDate = System.currentTimeMillis();
    this.modifyDate = System.currentTimeMillis();
    this.finished = false;
  }

  // public SNote(String title, String content, String location,
  // String evernoteGUID, String trophyNumber, String tripID)
  // {
  // super();
  // this.title = title;
  // this.content = content;
  // this.location = location;
  // this.evernoteGUID = evernoteGUID;
  // this.trophyNumber = trophyNumber;
  // this.tripID = tripID;
  // }

  // public SNote(String title, String content, String location, long
  // modifyDate,
  // long createDate, String evernoteGUID, ArrayList<SResource> resources,
  // ArrayList<String> tags, String trophyNumber, String tripID)
  // {
  // super();
  // this.title = title;
  // this.content = content;
  // this.location = location;
  // this.modifyDate = modifyDate;
  // this.createDate = createDate;
  // this.evernoteGUID = evernoteGUID;
  // this.resources = resources;
  // this.tags = tags;
  // this.trophyNumber = trophyNumber;
  // this.tripID = tripID;
  // }

  public void processResources(Note note)
  {
    Document doc = Jsoup.parse(this.content);

    Elements divs = doc.getElementsByAttribute("hash");
    // System.out.println("Images");
    for (final Element div : divs)
    {
      if (note.getResources() != null)
      {
        for (Resource resource : note.getResources())
        {
          System.out.println(div.attr("hash") + " "
              + EvernoteUtil.bytesToHex(resource.getData().getBodyHash()));
          if (div.attr("hash").equals(
              EvernoteUtil.bytesToHex(resource.getData().getBodyHash())))
          {
            this.addResource(new SResource(resource, div.attr("title")));
            break;
          }
        }
      }
    }
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

  public List<SResource> getSResource()
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

  public void setSResource(ArrayList<SResource> resources)
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
    values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_ISSET,
        encode(issetV));
    values.put(SouvenirContract.SouvenirNote.COLUMN_NAME_NOTE_TRIP_ID, tripID);
    return values;
  }

  public ArrayList<ContentValues> getResourcesContentValues()
  {
    ArrayList<ContentValues> returnArr = new ArrayList<ContentValues>();
    for (SResource sresource : resources)
    {
      returnArr.add(sresource.toContentValues());
    }
    return returnArr;
  }

  public Note toNote()
  {
    Note note = new Note();
    note.setTitle(this.getTitle());
    if (syncNum == -1)
    {
      issetV = EnumSet.allOf(isset.class);
      List<String> list = new ArrayList<String>();
      list.add(tripID);
      note.setTagNames(list);
    }
    else
    {
      note.setGuid(this.getEvernoteGUID());
    }
    for (SNote.isset isset : issetV)
    {
      switch (isset)
      {
      case content:
        note.setContent(this.content);
        break;
      case location:
        // note.seta
        break;
      case resources:
        for (SResource imageData : resources)
        {
          try
          {
            String f = imageData.getPath();
            Resource resource = new Resource();
            InputStream in;
            // System.out.println("f: " + f);
            in = new BufferedInputStream(new FileInputStream(f));
            FileData data = new FileData(EvernoteUtil.hash(in), new File(f));
            in.close();
            resource.setData(data);
            resource.setMime(imageData.getMime());
            ResourceAttributes attributes = new ResourceAttributes();
            // attributes.setFileName(imageData.getFileName);
            resource.setAttributes(attributes);
            note.addToResources(resource);
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
        break;
      case tags:
        break;
      case title:
        break;
      }
    }

    // if (NOTEBOOK_GUID != null)
    // {
    // note.setNotebookGuid(NOTEBOOK_GUID);
    // }
    // if (this.evernoteGUID != null)
    // note.setGuid(this.evernoteGUID);
    // note.setTitle(this.title);
    // note.setContent(this.content);
    note.setCreated(this.createDate);
    note.setUpdated(this.modifyDate);
    Calendar cal = Calendar.getInstance();

    cal.setTimeInMillis(this.modifyDate);

    java.util.Date date = cal.getTime();

    System.out.println(date);
    NoteAttributes attr = new NoteAttributes();
    attr.setPlaceName(location); // this is the official evernote loc name field
    LazyMap map = new LazyMap();
    map.putToFullMap("LOCATION", location); // this was the old & incorrect way
                                            // to store evernote loc
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
    dest.writeInt(this.id);
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
    dest.writeBundle(this.bundle);
    dest.writeInt(this.syncNum);
    dest.writeMap(r);
  }

  public void addResource(SResource resource)
  {
    resource.setNoteId(this.id);
    resources.add(resource);
    r.put(resource.getHash(), resource);
    bundle.putParcelable(resource.getHash(), resource);
  }

}
