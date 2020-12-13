package com.whatsapp.numbers.firebase.model;

public class MyChannels {
    private String name;
    private String image;
    private String url;
    private String type;

    public MyChannels() {
    }

    public MyChannels(String name, String image, String url , String type) {
        this.name = name;
        this.image = image;
        this.url = url;
        this.type = type;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
