package com.souvenir.android;

public class SNote
{
  public SNote(String noteTitle, String evernoteGUID, String noteContent,
      String noteLocation, String trophyNumber, String tripID,
      String noteModifyDate, String noteCreateDate)
  {
    super();
    this.noteTitle = noteTitle;
    this.evernoteGUID = evernoteGUID;
    this.noteContent = noteContent;
    this.noteLocation = noteLocation;
    this.trophyNumber = trophyNumber;
    this.tripID = tripID;
    this.noteModifyDate = noteModifyDate;
    this.noteCreateDate = noteCreateDate;
  }

  public SNote(String noteTitle, String noteContent, String noteLocation,
      String noteModifyDate, String noteCreateDate)
  {
    super();
    this.noteTitle = noteTitle;
    this.noteContent = noteContent;
    this.noteLocation = noteLocation;
    this.noteModifyDate = noteModifyDate;
    this.noteCreateDate = noteCreateDate;
  }

  String noteTitle;
  String evernoteGUID;
  String noteContent;
  String noteLocation;
  String trophyNumber;
  String tripID;
  String noteModifyDate;
  String noteCreateDate;

  public String getNoteTitle()
  {
    return noteTitle;
  }

  public void setNoteTitle(String noteTitle)
  {
    this.noteTitle = noteTitle;
  }

  public String getEvernoteGUID()
  {
    return evernoteGUID;
  }

  public void setEvernoteGUID(String evernoteGUID)
  {
    this.evernoteGUID = evernoteGUID;
  }

  public String getNoteContent()
  {
    return noteContent;
  }

  public void setNoteContent(String noteContent)
  {
    this.noteContent = noteContent;
  }

  public String getNoteLocation()
  {
    return noteLocation;
  }

  public void setNoteLocation(String noteLocation)
  {
    this.noteLocation = noteLocation;
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

  public String getNoteModifyDate()
  {
    return noteModifyDate;
  }

  public void setNoteModifyDate(String noteModifyDate)
  {
    this.noteModifyDate = noteModifyDate;
  }

  public String getNoteCreateDate()
  {
    return noteCreateDate;
  }

  public void setNoteCreateDate(String noteCreateDate)
  {
    this.noteCreateDate = noteCreateDate;
  }

}
