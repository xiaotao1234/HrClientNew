package com.huari.dataentry;

public class MessageEvent {
    private String messageString;

    public int getFilePosition() {
        return filePosition;
    }

    public void setFilePosition(int filePosition) {
        this.filePosition = filePosition;
    }

    private int filePosition;

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }

    public MessageEvent(String messageString,int filePosition) {
        this.messageString = messageString; this.filePosition = filePosition;
    }

}
