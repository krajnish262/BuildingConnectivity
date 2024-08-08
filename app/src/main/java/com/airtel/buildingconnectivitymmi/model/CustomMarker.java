package com.airtel.buildingconnectivitymmi.model;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class CustomMarker {
    private String title;
    private String snippet;
    private Double lat;
    private Double lng;
    private LatLng position;
    private Icon icon;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
