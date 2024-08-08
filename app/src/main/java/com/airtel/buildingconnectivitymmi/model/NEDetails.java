package com.airtel.buildingconnectivitymmi.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NEDetails{
    private String neName = "";
    private Double neLatitude = 0.0;
    private Double neLongitude = 0.0;
    private String neTotalPorts = "";
    private String neAvailablePorts = "";
    private String neUtilizedPort = "";
    private String neTechnology = "";
    private String neType = "";
    private String neDistanceFromHub = "";

    public String getNeName() {
        return neName;
    }

    public void setNeName(String neName) {
        this.neName = neName;
    }

    public Double getNeLatitude() {
        return neLatitude;
    }

    public void setNeLatitude(Double neLatitude) {
        this.neLatitude = neLatitude;
    }

    public Double getNeLongitude() {
        return neLongitude;
    }

    public void setNeLongitude(Double neLongitude) {
        this.neLongitude = neLongitude;
    }

    public String getNeTotalPorts() {
        return neTotalPorts;
    }

    public void setNeTotalPorts(String neTotalPorts) {
        this.neTotalPorts = neTotalPorts;
    }

    public String getNeAvailablePorts() {
        return neAvailablePorts;
    }

    public void setNeAvailablePorts(String neAvailablePorts) {
        this.neAvailablePorts = neAvailablePorts;
    }

    public String getNeUtilizedPort() {
        return neUtilizedPort;
    }

    public void setNeUtilizedPort(String neUtilizedPort) {
        this.neUtilizedPort = neUtilizedPort;
    }

    public String getNeTechnology() {
        return neTechnology;
    }

    public void setNeTechnology(String neTechnology) {
        this.neTechnology = neTechnology;
    }

    public String getNeType() {
        return neType;
    }

    public void setNeType(String neType) {
        this.neType = neType;
    }

    public String getNeDistanceFromHub() {
        return neDistanceFromHub;
    }

    public void setNeDistanceFromHub(String neDistanceFromHub) {
        this.neDistanceFromHub = neDistanceFromHub;
    }
}
