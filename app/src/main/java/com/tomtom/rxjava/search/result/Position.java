package com.tomtom.rxjava.search.result;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Position
{
    public double lat;
    public double lon;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("lat", lat)
                .add("lon", lon)
                .toString();
    }
}
