package net.senior.dadapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class FoldersModel implements Serializable {
    String name;
    String img;

    public FoldersModel() {
    }

    public FoldersModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


}
