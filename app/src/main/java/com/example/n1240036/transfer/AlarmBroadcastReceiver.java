package com.example.n1240036.transfer;
import android.content.*;
import android.widget.Toast;

import java.util.ArrayList;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent(context, ThirdActivity.class);
        ArrayList<Music_Item_NonImage> status = (ArrayList<Music_Item_NonImage>)intent.getSerializableExtra("PlayList");

        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("play_musics",status);
        context.startActivity(intent1);
    }
}