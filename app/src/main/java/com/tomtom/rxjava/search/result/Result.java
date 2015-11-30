package com.tomtom.rxjava.search.result;

public class Result
{
    public String type;
    public int id;
    public double score;
    public String info;
    public Poi poi;
    public Address address;
    public Position position;

    @Override
    public String toString() {
        return "D"+position.toString();
    }
}
