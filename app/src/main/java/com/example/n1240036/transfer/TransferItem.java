package com.example.n1240036.transfer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by n1240036 on 2016/11/04.
 */

public class TransferItem {
    public int transfer_cnt;
    public int time;
    public Date Departure;
    public Date Arrival;
    public ArrayList<String> stations=new ArrayList<>();
    public TransferItem(int ti,int tr,Date dep,Date arr,ArrayList<String> st){
        transfer_cnt=tr;
        time=ti;
        Departure=dep;
        Arrival=arr;
        stations=st;
    }
}
