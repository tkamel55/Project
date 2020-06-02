package com.example.kamel.project.Model;

public class CategorieModel {
    private String name;
    private int sets;
    private String url;

    String key;

    public CategorieModel() {

    }

    public CategorieModel(String name, int sets, String url, String key) {
        this.key = key;
        this.name = name;
        this.sets = sets;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
