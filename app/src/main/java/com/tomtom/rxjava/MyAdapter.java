package com.tomtom.rxjava;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.tomtom.rxjava.search.mapkit.GeoResult;
import com.tomtom.rxjava.search.result.Result;

import java.util.Collections;
import java.util.List;

/**
 * Created by defecins on 18/11/15.
 */
public class MyAdapter extends BaseAdapter {
    private List<CombinedResult> resultList = Collections.emptyList();

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public CombinedResult getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return System.identityHashCode(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.two_line_list_item, null);
            convertView.setTag(new ViewHolder(convertView));
        }

        ViewHolder convertViewTag = (ViewHolder)convertView.getTag();

        CombinedResult item = getItem(position);
        convertViewTag.text1.setText(String.format("%s(%s)", item.getName(), item.query));
        convertViewTag.text2.setText(item.getLatLongFormatted());
        convertViewTag.convertView.setBackgroundResource(item.getBackground());

        return convertView;
    }

    public void setResultList(List<CombinedResult> newResults) {
        resultList = newResults;
        notifyDataSetChanged();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public static class CombinedResult {
        public Result decartaResult;
        public GeoResult mapKitResult;
        public String query;

        private CombinedResult(Result decartaResult, GeoResult mapKitResult, String query) {
            this.decartaResult = decartaResult;
            this.mapKitResult = mapKitResult;
            this.query = query;
        }

        public String getLatLongFormatted()
        {
            if(decartaResult != null) {
                return String.format("(%f.2,%f.2)",decartaResult.position.lat,decartaResult.position.lon);
            }
            if(mapKitResult != null) {
                return String.format("(%f.2,%f.2)",mapKitResult.latitude, mapKitResult.longitude);
            }
            throw new IllegalStateException("CombinedResult does not have any result in it");
        }

        public String getName() {
            if(decartaResult != null) {
                return decartaResult.address.freeformAddress;
            }

            if(mapKitResult != null) {
                return mapKitResult.formattedAddress;
            }

            throw new IllegalStateException("CombinedResult does not have any result in it");
        }

        public int getBackground() {
            if(decartaResult != null)
                return android.R.color.holo_green_light;
            if(mapKitResult != null)
                return android.R.color.holo_blue_light;
            throw new IllegalStateException("CombinedResult does not have any result in it");
        }

        public static CombinedResult mapkit(GeoResult mapKitResult, String query) {
            return new CombinedResult(null, mapKitResult, query);
        }

        public static CombinedResult decarta(Result decartaResult, String query) {
            return new CombinedResult(decartaResult, null, query);
        }

        @Override
        public String toString() {
            if(decartaResult != null)
                return decartaResult.toString();
            if(mapKitResult != null)
                return mapKitResult.toString();
            throw new IllegalStateException("CombinedResult does not have any result in it");
        }
    }
}
