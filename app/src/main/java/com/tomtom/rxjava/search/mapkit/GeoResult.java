package com.tomtom.rxjava.search.mapkit;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.List;

public class GeoResult
{
    public double latitude;
    public double longitude;
    public String geohash;
    public String mapName;
    public String type;
    public String city;
    public String state;
    public String country;
    public String countryISO3;
    public String formattedAddress;
    public boolean isCensusMicropolitanFlag;
    public int widthMeters;
    public int heightMeters;
    public double score;
    public double confidence;
    public int iteration;
    public String street;
    public List<String> alternativeStreetName;
    public String postcode;
    public String cbsaCode;
    public String censusBlock;
    public String censusTract;
    public String censusStateCode;
    public String censusFipsCountyCode;
    public String censusFipsMinorCivilDivision;
    public String censusFipsPlaceCode;

    @Override
    public String toString() {
        return "M"+MoreObjects.toStringHelper(this)
                .add("longitude", longitude)
                .add("latitude", latitude)
                .toString();
    }
}
