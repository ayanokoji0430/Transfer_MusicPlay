package com.example.n1240036.transfer;

import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.n1240036.transfer.R;

import java.util.List;

/**
 * Created by n1240036 on 2016/11/21.
 */

public class MusicAdapter extends ArrayAdapter<Music_Item>{
    private LayoutInflater layoutInflater_;

    public MusicAdapter(Context context, int textViewResourceId, List<Music_Item> objects) {
        super(context, textViewResourceId, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Music_Item item = (Music_Item) getItem(position);

        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.music_list_item, null);
        }

        TextView textView;
        textView = (TextView)convertView.findViewById(R.id.music_list_item);
        textView.setText(item.getTitleData());

        return convertView;
    }
}
