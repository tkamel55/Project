package com.example.kamel.project.Model;

public class CategoryVideo {
    private String name, id ;
    private int nVideo ;

    public CategoryVideo(String name, String id, int nVideo) {
        this.name = name;
        this.id = id;
        this.nVideo = nVideo;
    }

    public CategoryVideo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getnVideo() {
        return nVideo;
    }

    public void setnVideo(int nVideo) {
        this.nVideo = nVideo;
    }
}
