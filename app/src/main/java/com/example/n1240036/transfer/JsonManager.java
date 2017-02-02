package com.example.n1240036.transfer;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class JsonManager implements LoaderManager.LoaderCallbacks<JSONObject> {

    private MainActivity activity;
    private String url;
    private boolean init_state=false;
    private int form_num;

    public interface MyClassCallbacks {
        public void callbackMethod(List<String> res,int form_num);
    }

    private MyClassCallbacks _myClassCallbacks;

    public void setCallbacks(MyClassCallbacks myClassCallbacks){
        _myClassCallbacks = myClassCallbacks;
    }


    public JsonManager(MainActivity activity) {
        this.activity = activity;
    }

    public void runDataLoader(String ApiKey,String station_name,int form_num) {
        String station_encode="";
        try {
            station_encode= URLEncoder.encode(station_name,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.form_num=form_num;
        this.url ="https://api.apigw.smt.docomo.ne.jp/ekispertCorp/v1/stationLight?APIKEY="+ApiKey+"&name="+station_encode;
        this.activity.getLoaderManager().restartLoader(1,null, this);

    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        JsonLoader jsonLoader = new JsonLoader(this.activity.getApplicationContext(), this.url);

        jsonLoader.forceLoad();
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {
        loader.reset();
        List<String> stations=new ArrayList<>();
        try {

            JSONArray points = data.getJSONObject("ResultSet").getJSONArray("Point");

            for(int j=0;j<points.length();j++){
                if(j>10)break;
                stations.add(points.getJSONObject(j).getJSONObject("Station").getString("Name"));

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

        _myClassCallbacks.callbackMethod(stations,form_num);


    }

    @Override
    public void onLoaderReset(Loader<JSONObject> loader) {
    }
}