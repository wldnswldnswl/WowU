package com.capstone.wowu;

public class infoItem {
    String image;
    String name;

    public infoItem(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

}
