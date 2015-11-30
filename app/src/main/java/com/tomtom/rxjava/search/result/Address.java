package com.tomtom.rxjava.search.result;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Address
{
    public String streetNumber;
    public String streetName;
    public String municipality;
    public String postalCode;
    public String countryCode;
    public String country;
    public String countryCodeISO3;
    public String freeformAddress;
    public String municipalitySubdivision;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("streetNumber", streetNumber)
                .add("streetName", streetName)
                .add("municipality", municipality)
                .add("postalCode", postalCode)
                .add("countryCode", countryCode)
                .add("country", country)
                .add("countryCodeISO3", countryCodeISO3)
                .add("freeformAddress", freeformAddress)
                .add("municipalitySubdivision", municipalitySubdivision)
                .toString();
    }
}
