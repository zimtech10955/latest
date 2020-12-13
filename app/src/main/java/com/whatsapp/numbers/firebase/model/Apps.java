package com.whatsapp.numbers.firebase.model;

public class Apps {
    private String icon;
    private String url;

    public Apps() {
    }

    public Apps(String icon, String url) {
        this.icon = icon;
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
