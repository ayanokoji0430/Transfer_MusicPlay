package com.example.n1240036.transfer;
import android.content.*;
import android.widget.Toast;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent(context, SubActivity.class);
        String status = intent.getStringExtra("ride_t");
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("ride_time",Integer.parseInt(status));
        context.startActivity(intent1);
    }
}