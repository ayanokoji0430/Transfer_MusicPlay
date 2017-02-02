package com.example.n1240036.transfer;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by n1240036 on 2016/11/09.
 */

public class ThirdActivity extends AppCompatActivity implements OnCompletionListener{

    private MediaPlayer mp = null;
    private ArrayList<Music_Item_NonImage>play_list;
    private int current_song_num;
    private TextView title;
    private TextView  artist;
    private ImageView album_art;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        title=(TextView)findViewById(R.id.Music_Title);
        artist=(TextView)findViewById(R.id.Artist_name);
        album_art=(ImageView)findViewById(R.id.imageView4);
        current_song_num=0;
        play_list=(ArrayList<Music_Item_NonImage>) getIntent().getSerializableExtra("play_musics");
        mp = new MediaPlayer();
        mp.setOnCompletionListener(this);
        set_song_path();

        play(mp);

    }
    @Override
    protected void onResume() {
        super.onResume();
        requestVisibleBehind(true);
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
            return;
        }
        ViewSet();
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp_tmp) {
        if(current_song_num>=play_list.size()-1){
            return;
        }else{
            current_song_num++;
        }
        mp_tmp.stop();
        mp_tmp.reset();
        set_song_path();
        play(mp_tmp);
    }

    private void ViewSet(){
        Album_Art artwork=new Album_Art();
        Bitmap bmp=artwork.getArtWork(((Music_Item_NonImage)play_list.get(current_song_num)).getPathData());
        album_art.setImageBitmap(bmp);
        title.setText(((Music_Item_NonImage)play_list.get(current_song_num)).getTitleData());
        artist.setText(((Music_Item_NonImage)play_list.get(current_song_num)).getArtst());


    }
    public void Exit_Click(View v){

    }





}
