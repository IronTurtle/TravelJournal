package com.souvenir.android;

import java.util.ArrayList;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

public class SNote implements Parcelable
{
  int id;
  String title;
  String content;
  String location;
  String modifyDate;
  String createDate;
  String evernoteGUID;
  ArrayList<Resources> resources;
  ArrayList<String> tags;
  String trophyNumber;
  String tripID;
  int syncNum;

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

  public SNote(String title, String content, String location,
      String modifyDate, String createDate, String evernoteGUID,
      ArrayList<Resources> resources, ArrayList<String> tags,
      String trophyNumber, String tripID)
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

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeInt(id);
    dest.writeString(this.title);
    dest.writeString(this.content);
    dest.writeString(this.location);
    dest.writeString(this.modifyDate);
    dest.writeString(this.createDate);
    dest.writeString(this.evernoteGUID);
    dest.writeList(this.resources);
    dest.writeList(this.tags);
    dest.writeString(this.trophyNumber);
    dest.writeString(this.tripID);
  }

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

  @SuppressWarnings("unchecked")
  public SNote(Parcel in)
  {
    super();
    this.id = in.readInt();
    this.title = in.readString();
    this.content = in.readString();
    this.location = in.readString();
    this.modifyDate = in.readString();
    this.createDate = in.readString();
    this.evernoteGUID = in.readString();
    this.resources = in.readArrayList(String.class.getClassLoader());
    this.tags = in.readArrayList(String.class.getClassLoader());
    this.trophyNumber = in.readString();
    this.tripID = in.readString();
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getContent()
  {
    return content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public String getLocation()
  {
    return location;
  }

  public void setLocation(String location)
  {
    this.location = location;
  }

  public String getModifyDate()
  {
    return modifyDate;
  }

  public void setModifyDate(String modifyDate)
  {
    this.modifyDate = modifyDate;
  }

  public String getCreateDate()
  {
    return createDate;
  }

  public void setCreateDate(String createDate)
  {
    this.createDate = createDate;
  }

  public String getEvernoteGUID()
  {
    return evernoteGUID;
  }

  public void setEvernoteGUID(String evernoteGUID)
  {
    this.evernoteGUID = evernoteGUID;
  }

  public ArrayList<Resources> getResources()
  {
    return resources;
  }

  public void setResources(ArrayList<Resources> resources)
  {
    this.resources = resources;
  }

  public ArrayList<String> getTags()
  {
    return tags;
  }

  public void setTags(ArrayList<String> tags)
  {
    this.tags = tags;
  }

  public String getTrophyNumber()
  {
    return trophyNumber;
  }

  public void setTrophyNumber(String trophyNumber)
  {
    this.trophyNumber = trophyNumber;
  }

  public String getTripID()
  {
    return tripID;
  }

  public void setTripID(String tripID)
  {
    this.tripID = tripID;
  }

  public int getSyncNum()
  {
    return syncNum;
  }

  public void setSyncNum(int syncNum)
  {
    this.syncNum = syncNum;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

}
