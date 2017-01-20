package com.example.n1240036.transfer;


import android.app.*;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;

/**
 * Created by n1240036 on 2016/11/09.
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SubActivity extends AppCompatActivity implements OnCompletionListener{

    private MediaPlayer mp = null;
    private ArrayList<Music_Item>play_list;
    private int current_song_num;
    private int ride_time;
    private long play_duration;
    private int before_num;

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
        current_song_num=0;
        before_num=0;
        statusBarNitify();
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,
                new String[]{
                        MediaStore.Audio.Media.TITLE ,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DURATION
                },
                null ,
                null ,
                null
        );
        ArrayList<Music_Item> Music_items= new ArrayList<>();
        play_list=new ArrayList<>();
        ride_time = ((int) getIntent().getSerializableExtra("ride_time"))*60;
        TextView text_view = (TextView) findViewById(R.id.textView);

        while(cursor.moveToNext()){
            if( cursor.getLong(cursor.getColumnIndex( MediaStore.Audio.Media.DURATION)) < 10000 ){continue;}
            Music_Item mi=new Music_Item();
            mi.setTitleData(cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Media.TITLE)));
            mi.setPathData(cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Media.DATA)));
            mi.setDurationData(cursor.getLong(cursor.getColumnIndex( MediaStore.Audio.Media.DURATION))/1000);
            Music_items.add(mi);
        }
        play_list=shuffle(Music_items);
        MusicAdapter musicAdapter=new MusicAdapter(this, R.layout.music_list_item,play_list);

        text_view.setText(play_duration/60+"分 "+play_duration%60+"秒");
        mp = new MediaPlayer();
        mp.setOnCompletionListener(this);
        set_song_path();

        ListView listview=(ListView)findViewById(R.id.list2);
        listview.setSelector(new ColorDrawable(Color.rgb(153,204,0)));
        listview.setAdapter(musicAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view, int pos, long id) {  mp.stop();
                mp.reset();
                current_song_num=pos;
                set_song_path();
                play(mp);
                View a=view;
                int e=1;
                view.setSelected(true);
                int b=0;
            }
        });
        play(mp);
    }
    @Override
    protected void onResume() {
        super.onResume();
        requestVisibleBehind(true);
    }


    protected void play_Click(View v) {
        if(mp.isPlaying()){
            mp.stop();
            ((Button)findViewById(R.id.media_play_stop)).setBackgroundResource(android.R.drawable.ic_media_play);

        }else{
            play(mp);
        }
        move_selected();
    }

    protected void previous_Click(View v) {
        before_num=current_song_num;
        if(current_song_num<=0){
            current_song_num=play_list.size()-1;
        }else{
            current_song_num--;
        }
        mp.stop();
        mp.reset();
        set_song_path();
        play(mp);
        move_selected();
    }

    protected void next_Click(View v) {
        before_num=current_song_num;
        if(current_song_num>=play_list.size()-1){
            current_song_num=0;
        }else{
            current_song_num++;
        }
        mp.stop();
        mp.reset();
        set_song_path();

        String a="A";
        play(mp);
        move_selected();
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.v("MediaPlayer", "onDestroy");
        if (mp.isPlaying()) {
            mp.stop();
         }
        mp.release();
     }

    private void set_song_path(){
        try {
            mp.setDataSource(play_list.get(current_song_num).getPathData());
        } catch (IOException e) {
            aleart("ファイルを再生できません");
        }
    }
    private void aleart(String s){
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle("alert");
        alertDlg.setMessage(s);
        alertDlg.create().show();
    }

    private void play(MediaPlayer mp_tmp){
        try {
            mp_tmp.prepare();
        } catch (IOException e) {
            aleart("ファイルを再生できません");
        }
        mp.start();
        ((Button)findViewById(R.id.media_play_stop)).setBackgroundResource(android.R.drawable.ic_media_pause);
    }

    private ArrayList<Music_Item> shuffle(ArrayList<Music_Item> musics){
        boolean successful =false;
        ArrayList<Music_Item> max=new ArrayList<Music_Item>();
        int max_num=0;
        Random rnd = new Random();
        for(int i = 0; i<300&&!successful; i++){
            Collections.shuffle(musics);
            int tmp_num=0;

            ArrayList<Music_Item> tmp=new ArrayList<Music_Item>();
            int random_push_num=rnd.nextInt(musics.size());
            Music_Item music=musics.get(random_push_num);
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
        ArrayList<Music_Item> result=max;
        play_duration=max_num;
        return result;
    }

    @Override
    public void onCompletion(MediaPlayer mp_tmp) {
        before_num=current_song_num;
        if(current_song_num>=play_list.size()-1){
            return;
        }else{
            current_song_num++;
        }
        mp_tmp.stop();
        mp_tmp.reset();
        set_song_path();
        play(mp_tmp);
        move_selected();
    }

    public void move_selected(){
        ((ListView)findViewById(R.id.list2)).getChildAt(before_num).setSelected(false);
        ((ListView)findViewById(R.id.list2)).getChildAt(current_song_num).setSelected(true);
    }

    private void statusBarNitify() {

        Intent resultIntent = new Intent(String.valueOf(this));

        PendingIntent resultPendingIntent =PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =(NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("アラーム起動")
                        .setContentText("音楽がランダムで再生されています")
                        .setAutoCancel(true)

                .setTicker("notification is displayed !!");

        mBuilder.setContentIntent(resultPendingIntent);
        //mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;

        int mNotificationId = 001;

        NotificationManager mNotifyMgr =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

}
