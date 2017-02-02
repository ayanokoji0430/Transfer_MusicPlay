package com.example.n1240036.transfer;


import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;
import java.net.URI;

/**
 * Created by n1240036 on 2016/11/17.
 */

public class Music_Item extends Music_Item_NonImage
{

    private Bitmap artwork;

    public void setArtwork(Bitmap artwork) { this.artwork=artwork; }
    public Bitmap getartwork() {
        return this.artwork;
    }

}
