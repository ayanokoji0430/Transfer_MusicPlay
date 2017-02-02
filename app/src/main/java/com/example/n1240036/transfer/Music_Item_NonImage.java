package com.example.n1240036.transfer;


import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by n1240036 on 2016/11/17.
 */

public class Music_Item_NonImage implements Serializable
{
    private String title;
    private String artst;
    private String path;
    private long duration;

    public void setTitleData(String title_text) {title = title_text;  }
    public String getTitleData() {  return title; }

    public void setArtist(String artist) {this.artst = artist;  }
    public String getArtst() {  return this.artst; }

    public void setPathData(String path_text) {
        path = path_text;
    }
    public String getPathData() {
        return path;
    }

    public void setDurationData(long duration_long) {
        duration = duration_long;
    }
    public long getDurationData() {
        return duration;
    }



}
