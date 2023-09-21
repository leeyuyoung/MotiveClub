package com.motiveclub;

import java.io.Serializable;

public class Note implements Serializable {
    private String noteId; // 노트의 고유 식별자
    private String title;
    private String contents;
    private String date;

    private String key;
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Note(){}

    public Note(String title, String contents, String date){
        this.title = title;
        this.contents = contents;
        this.date = date;
    }

    // Getter 및 Setter 메소드
    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }




}
