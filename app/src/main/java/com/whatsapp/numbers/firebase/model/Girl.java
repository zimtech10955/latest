package com.whatsapp.numbers.firebase.model;

public class Girl {
    private String name;
    private String image;
    private String age;
    private String country;
    //private String number;

    public Girl() {
    }

    public Girl(String name, String image, String age, String country) {
        this.name = name;
        this.image = image;
        this.age = age;
        this.country = country;
      //  this.number = number;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
