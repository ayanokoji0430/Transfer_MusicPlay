package com.example.n1240036.transfer;


import android.app.*;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;

/**
 * Created by n1240036 on 2016/11/09.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class SubActivity extends AppCompatActivity {

    private MediaPlayer mp = null;
    private ArrayList<Music_Item_NonImage> play_list_nonImage;
    private MusicAdapter musicAdapter;
    private long play_duration;
    private int ride_time;
    private Date departure=null;
    private AlarmManager am;
    private PendingIntent pending;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
               */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        play_duration=0;
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,
                new String[]{
                        MediaStore.Audio.Media.TITLE ,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST
                },
                null ,
                null ,
                null
        );
        ArrayList<Music_Item_NonImage> Music_items= new ArrayList<Music_Item_NonImage>();
        ArrayList<Music_Item> play_list=new ArrayList<Music_Item>();
        play_list_nonImage=new ArrayList<Music_Item_NonImage>();
        Intent intent = getIntent();
        Departure_RideTime dr = (Departure_RideTime) intent.getSerializableExtra("Departure_RideTime");
        ride_time = (dr.getride_time())*60;
        departure=dr.getDeparture();
        TextView text_view = (TextView) findViewById(R.id.textView);

        while(cursor.moveToNext()){

            if( cursor.getLong(cursor.getColumnIndex( MediaStore.Audio.Media.DURATION)) < 10000 ){continue;}
            Music_Item_NonImage mi=new Music_Item_NonImage();
            mi.setTitleData(cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Media.TITLE)));
            mi.setArtist(cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST)));
            mi.setPathData(cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Media.DATA)));
            mi.setDurationData(cursor.getLong(cursor.getColumnIndex( MediaStore.Audio.Media.DURATION))/1000);
            Music_items.add(mi);
        }
        play_list_nonImage=shuffle(Music_items);
        Album_Art album_a=new Album_Art();
        for (Music_Item_NonImage item_nonImage:play_list_nonImage  ) {
            Music_Item item=new Music_Item();
            Bitmap artwork=album_a.getArtWork(item_nonImage.getPathData());
            item.setPathData(item_nonImage.getPathData());
            item.setArtist(item_nonImage.getArtst());
            item.setTitleData(item_nonImage.getTitleData());
            item.setDurationData(item.getDurationData());
            item.setArtwork(artwork);
            play_list.add(item);
        }


        musicAdapter=new MusicAdapter(this, R.layout.music_list_item,play_list);

        text_view.setText(play_duration/60+"min "+play_duration%60+"s");

        ListView listview=(ListView)findViewById(R.id.list2);
        listview.setSelector(new ColorDrawable(Color.rgb(153,204,0)));
        listview.setAdapter(musicAdapter);
    }

    private ArrayList<Music_Item_NonImage> shuffle(ArrayList<Music_Item_NonImage> musics){
        boolean successful =false;
        ArrayList<Music_Item_NonImage> max=new ArrayList<Music_Item_NonImage>();
        int max_num=0;
        Random rnd = new Random();
        for(int i = 0; i<300&&!successful; i++){
            Collections.shuffle(musics);
            int tmp_num=0;

            ArrayList<Music_Item_NonImage> tmp=new ArrayList<Music_Item_NonImage>();
            int random_push_num=rnd.nextInt(musics.size());
            Music_Item_NonImage music=musics.get(random_push_num);
            while(tmp_num+music.getDurationData()<ride_time){
                tmp.add(music);
                tmp_num+=music.getDurationData();
                random_push_num=rnd.nextInt(musics.size());
                music=musics.get(random_push_num);
            }

            if(max_num==0||tmp_num>max_num){
                max=tmp;
                max_num=tmp_num;
            }
            long sub=ride_time-max_num;
            if(sub<=30) {
                successful = true;
            }
        }
        ArrayList<Music_Item_NonImage> result=max;
        play_duration=max_num;
        return result;
    }
    protected void start_alarm(View v){
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(SubActivity.this);
        alertDlg.setTitle("再生の予約");
        alertDlg.setMessage("");
        alertDlg.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(departure);
                        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
                        intent.putExtra("PlayList",play_list_nonImage);
                        pending = PendingIntent.getBroadcast(SubActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        am = (AlarmManager) getSystemService(ALARM_SERVICE);
                        am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
                    }
                });
        alertDlg.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        alertDlg.create().show();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (am != null) {
                am.cancel(pending);
            }
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
