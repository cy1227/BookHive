package com.example;

public class Book {
    private String title;
    private String author;
    private String content;
    private String addTime;


    public Book(String title, String author, String content, String addTime) {
        this.title = title;
        if(author == "") this.author = "無資料";
        else this.author = author;
        this.content = content;
        this.addTime = addTime;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getAddTime() {
        return addTime;
    }

    public String toString() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

}
