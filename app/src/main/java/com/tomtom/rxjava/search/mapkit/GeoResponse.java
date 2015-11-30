package com.tomtom.rxjava.search.mapkit;

import java.util.List;

public class GeoResponse
{
    public String svnRevision;
    public int count;
    public String version;
    public String duration;
    public String debugInformation;
    public String consolidatedMaps;
    public List<ResultStatusList> resultStatusList;
    public List<GeoResult> geoResult;
}
