package com.example.n1240036.transfer;

import android.app.*;
import android.content.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.view.View;
import android.app.AlertDialog;
import org.json.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject>{

    private Date departure_format;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //((AutoCompleteTextView)findViewById(R.id.search_from)).setThreshold(4);
    }

    public void onButtonClick(View view){
        getLoaderManager().restartLoader(1, null, MainActivity.this);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {

        String from="";
        String to="";
        String APIKEY="2e6759695a335133536d3777494959335a2e356d6936332e4674672f615a44675457757236723075767338";
        try {
            from= URLEncoder.encode(((EditText)findViewById(R.id.search_from)).getText().toString(),"utf-8");
            to= URLEncoder.encode(((EditText)findViewById(R.id.search_to)).getText().toString(),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer urlText =new StringBuffer("https://api.apigw.smt.docomo.ne.jp/ekispertCorp/v1/searchCourse?APIKEY=");
        urlText.append(APIKEY);
        urlText.append("&from=");
        urlText.append(from);
        urlText.append("&to=");
        urlText.append(to);
        JsonLoader jsonLoader = new JsonLoader(this, urlText.toString());

        jsonLoader.forceLoad();
        return  jsonLoader;
    }

    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {
        final ArrayList<TransferItem> ti=new ArrayList<TransferItem>();
        ListView listview=(ListView)findViewById(R.id.list1);
        ArrayAdapter adapter=new ArrayAdapter<String>(this, R.layout.transfer_list_item);
        listview.setAdapter(adapter);
        if (data != null) {
            String departure="";
            String arrival="";
            try {
                JSONArray courses= data.getJSONObject("ResultSet").getJSONArray("Course");
                for(int i=0;i<courses.length();i++){
                    String station_name="";
                    JSONObject route=courses.getJSONObject(i).getJSONObject("Route");
                    int transfer=Integer.parseInt(route.getString("transferCount"));
                    JSONArray points=route.getJSONArray("Point");
                    ArrayList<String> stations=new ArrayList<String>();
                    for(int j=0;j<points.length();j++){
                        stations.add(points.getJSONObject(j).getJSONObject("Station").getString("Name"));
                    }
                    if(transfer!=0){
                        JSONArray lines=route.getJSONArray("Line");
                        departure=lines.getJSONObject(0).getJSONObject("DepartureState").getJSONObject("Datetime").getString("text");
                        arrival=lines.getJSONObject(lines.length()-1).getJSONObject("ArrivalState").getJSONObject("Datetime").getString("text");
                    }else{
                        JSONObject line=route.getJSONObject("Line");
                        departure=line.getJSONObject("DepartureState").getJSONObject("Datetime").getString("text");
                        arrival=line.getJSONObject("ArrivalState").getJSONObject("Datetime").getString("text");
                    }
                    departure_format=date_format(departure);
                    Date arrivale_format=date_format(arrival);
                    SimpleDateFormat sdf_jpn_locale = new SimpleDateFormat("yyyy年MM月dd日(E)　HH時mm分発");
                    String clock=sdf_jpn_locale.format(departure_format);
                    int time=time_sum(route.getString("timeOnBoard"),route.getString("timeWalk"),route.getString("timeOther"));
                    ti.add(new TransferItem(time,transfer,departure_format,arrivale_format,stations));
                    StringBuffer sb=new StringBuffer("");
                    for(int j=0;j<stations.size();j++){
                        sb.append(stations.get(j));
                        if(j<stations.size()-1){
                            sb.append("\n↓\n");}else{
                            sb.append("\n");
                        }
                    }
                    adapter.add("時間"+String.valueOf(time)+"\n乗り換え"+String.valueOf(transfer)+"\n"+clock+"\n"+station_name+"\n"+sb);
                }
            } catch (JSONException e) {
                Log.d("onLoadFinished","JSONのパースに失敗しました。 JSONException=" + e);
            }
        }else{
            Log.d("onLoadFinished", "onLoadFinished error!");
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view, final int pos, long id) {
                view.setSelected(true);
                final int ride_time=ti.get(pos).time;
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(MainActivity.this);
                alertDlg.setTitle("プレイリストの生成");
                alertDlg.setMessage(String.valueOf(ride_time)+"分のプレイリストを生成します");
                alertDlg.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(ti.get(pos).Departure);
                                Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
                                intent.putExtra("ride_t",String.valueOf(ride_time));
                                PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

                                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);

                                Toast.makeText(getApplicationContext(), "Set Alarm ", Toast.LENGTH_SHORT).show();

                            }
                        });
                alertDlg.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancelボタン押したとき
                            }
                        });
                alertDlg.create().show();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<JSONObject> loader) {
        // 処理なし
    }
    public int time_sum(String onBoard,String walk,String other){
        int sum=Integer.parseInt(onBoard)+Integer.parseInt(walk)+Integer.parseInt(other);

        return sum;
    }

    public Date date_format(String s)  {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        Date format_result = null;
        try {
            format_result = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int a=1;
        return format_result;
    }
}
