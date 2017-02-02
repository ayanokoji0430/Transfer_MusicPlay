package com.example.n1240036.transfer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

/**
 * Created by n1240036 on 2017/02/02.
 */

public class Album_Art {

    public Bitmap getArtWork(String filePath){

        Bitmap bm = null;

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filePath);

        byte[] data = mmr.getEmbeddedPicture();

        // 画像が無ければnullになる
        if (null != data) {
            bm = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return bm;
    }
}