package net.senior.dadapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Model {

    @PrimaryKey
    public long primaryKey;
    public String id;
   public String text;
    public String img;
    public String path;

    public Model(String text) {
        this.text = text;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public Model() {
    }

    public Model(String id, String text) {
        this.id = id;
        text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
