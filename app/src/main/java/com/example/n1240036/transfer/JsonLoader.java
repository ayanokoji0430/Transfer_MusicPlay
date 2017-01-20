package com.example.n1240036.transfer;


import android.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import java.io.*;
import java.net.*;


/**
 * Created by n1240036 on 2016/10/31.
 */
 class  JsonLoader extends AsyncTaskLoader<JSONObject> {
    private String urlText;

    public JsonLoader(Context context, String urlText){
        super(context);
        this.urlText = urlText;
    }

    @Override
    public JSONObject loadInBackground(){
        HttpURLConnection connection = null;

        try{
            URL url = new URL(urlText);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
        }
        catch (MalformedURLException exception){
            // 処理なし
        }
        catch (IOException exception){
            // 処理なし
        }

        try{
            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1){
                if (length > 0){
                    outputStream.write(buffer, 0, length);
                }
            }

            JSONObject json = new JSONObject(new String(outputStream.toByteArray()));
            return json;
        }
        catch (IOException exception){
            // 処理なし
            String a="a";
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
