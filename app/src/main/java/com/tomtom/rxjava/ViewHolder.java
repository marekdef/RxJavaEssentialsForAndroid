package com.tomtom.rxjava;

import android.view.View;
import android.widget.TextView;

/**
 * Created by defecins on 18/11/15.
 */
public class ViewHolder {
    public final TextView text1;
    public final TextView text2;
    public final View convertView;

    public ViewHolder(View convertView) {
        this.convertView = convertView;
        text1 = (TextView) convertView.findViewById(android.R.id.text1);
        text2 = (TextView) convertView.findViewById(android.R.id.text2);
    }
}
