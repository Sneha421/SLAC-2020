package com.example.gupshup.Chat;

public class ChatListModel
{
    private String userID;
    private String userName;
    private String lastMessage;
    private String photoName;
    private String unreadMessageCount;
    private String lastMessageTime;

    public ChatListModel(String userID, String userName, String lastMessage, String photoName, String unreadMessageCount, String lastMessageTime)
    {
        this.userID = userID;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.photoName = photoName;
        this.unreadMessageCount = unreadMessageCount;
        this.lastMessageTime = lastMessageTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(String unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
