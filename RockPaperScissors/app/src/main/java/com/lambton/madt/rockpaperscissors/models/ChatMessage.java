package com.lambton.madt.rockpaperscissors.models;

/**
 * Created by macstudent on 2018-04-10.
 */

public class ChatMessage {
    public String name;
    public String message;
    public String date;


    public ChatMessage() {

    }
    public ChatMessage(String name, String message, String date) {
        this.name = name;
        this.message = message;
        this.date = date;
    }
    public String getMessage() {
        return this.message;
    }
    public String getName() {
        return this.name;
    }
    public void setMessage(String s) {
        this.message = s;
    }
    public void setName(String n) {
        this.name = n;
    }

    public String getDate() {
        return this.date;
    }
}

