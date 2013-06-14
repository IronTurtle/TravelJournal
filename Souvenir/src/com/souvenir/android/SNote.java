package com.souvenir.android;

public class SNote
{
  String noteTitle;
  String evernoteGUID;
  String noteContent;
  String noteLocation;
  String trophyNumber;
  String tripID;
  String noteDate;

  public SNote(String noteName, String evernoteGUID, String noteContent,
      String noteLocation, String trophyNumber, String tripID, String noteDate)
  {
    super();
    this.noteTitle = noteName;
    this.evernoteGUID = evernoteGUID;
    this.noteContent = noteContent;
    this.noteLocation = noteLocation;
    this.trophyNumber = trophyNumber;
    this.tripID = tripID;
    this.noteDate = noteDate;
  }

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

  public String getNoteDate()
  {
    return noteDate;
  }

  public void setNoteDate(String noteDate)
  {
    this.noteDate = noteDate;
  }

}
