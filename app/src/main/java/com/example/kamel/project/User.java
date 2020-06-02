package com.example.kamel.project;

class User {
    private long userID;
    private String email;
    private String password;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;

    public User(long userID, String email, String password, boolean isActive, String createdAt, String updatedAt) {
        this.userID = userID;
        this.email = email;

        this.password = password;

        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User() {
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String  getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String  createdAt) {
        this.createdAt = createdAt;
    }

    public String  getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String  updatedAt) {
        this.updatedAt = updatedAt;
    }
}
