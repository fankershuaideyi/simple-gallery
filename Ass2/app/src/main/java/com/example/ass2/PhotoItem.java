package com.example.ass2;

public class PhotoItem {
    private String photoPath;
    private long timeAdd;
    private long id;
    private int width;
    private int height;

    private int orientation;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public long getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public int getOrientation() {
        return orientation;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public long getTimeAdd() {
        return timeAdd;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setTimeAdd(long timeAdd) {
        this.timeAdd = timeAdd;
    }
}
