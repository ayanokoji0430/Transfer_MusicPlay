package com.example.n1240036.transfer;




/**
 * Created by n1240036 on 2016/11/17.
 */

public class Music_Item
{
    private String title;
    private String path;
    private long duration;

    public void setTitleData(String title_text) {
        title = title_text;
    }

    public String getTitleData() {
        return title;
    }
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
