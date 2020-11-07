package com.example.gupshup.FindFriends;

public class FindFriendModel
{
    String userName;
    String photoName;
    String userID;
    boolean requestStatus;

    public FindFriendModel(String userName, String photoName, String userID, boolean requestStatus)
    {
        this.userName = userName;
        this.photoName = photoName;
        this.userID = userID;
        this.requestStatus = requestStatus;
    }


    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPhotoName()
    {
        return photoName;
    }

    public void setPhotoName(String photoName)
    {
        this.photoName = photoName;
    }

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public boolean isRequestStatus()
    {
        return requestStatus;
    }

    public void setRequestStatus(boolean requestStatus)
    {
        this.requestStatus = requestStatus;
    }
}
