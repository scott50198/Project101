package com.hsh.project101;

public class MusicData {
    private String m4aUrl;
    private String imgUrl;
    private String name;
    private String albumName;
    private int durationInMillis;

    public String getM4aUrl() {
        return m4aUrl;
    }

    public void setM4aUrl(String m4aUrl) {
        this.m4aUrl = m4aUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getDurationInMillis() {
        return durationInMillis;
    }

    public void setDurationInMillis(int durationInMillis) {
        this.durationInMillis = durationInMillis;
    }

    @Override
    public String toString() {
        return "MusicData{" +
                "m4aUrl='" + m4aUrl + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", name='" + name + '\'' +
                ", albumName='" + albumName + '\'' +
                ", durationInMillis=" + durationInMillis +
                '}';
    }
}
