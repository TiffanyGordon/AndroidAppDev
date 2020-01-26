package com.ariana.notepad;

public class Notes {

    private String title;
    private String content;
    private String timestamp;

    public Notes (String title, String content, String timestamp) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;

    }

    public String getTitle() {
        return title;
    }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }

    public String toString() {return title + ": " + content + ":" + timestamp; }
}
