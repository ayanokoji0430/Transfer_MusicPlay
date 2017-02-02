package com.example.n1240036.transfer;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by n1240036 on 2017/02/01.
 */

public class Departure_RideTime implements Serializable {
    private Date departure;
    private int ride_time;
    public Date getDeparture() {
        return departure;
    }
    public void setDeparture(Date departure) {
        this.departure = departure;
    }
    public int getride_time() {
        return ride_time;
    }
    public void setride_time(int ride_time) {
        this.ride_time = ride_time;
    }
}
