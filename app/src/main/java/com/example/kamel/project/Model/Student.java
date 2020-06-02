package com.example.kamel.project.Model;

import android.net.Uri;

public class Student {

    public String Name;
    public String Email;
    private String user_key;

    public Student()
    {
    }

    public Student(String email, String name) {
        this.Email = email;
        this.Name = name;
    }

    public String getUser_name() {
        return Name;
    }


    public String getUser_email() {
        return Email;
    }


    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {

        this.user_key = user_key;


    }
}
