package com.example.androidar.tour_info;

import java.net.URL;

public class Point {
    private String longitude;
    private String latitude;
    private String name;
    private int order;
    private int id;

    private String text;
    private URL[] foto;

    public Point(String longitude, String latitude, String name, int order, int id) {
        this.longitude=longitude;
        this.latitude=latitude;
        this.name=name;
        this.order=order;
        this.id=id;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public String getText() {
        return text;
    }

    public URL[] getFoto() {
        return foto;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFoto(URL[] foto) {
        this.foto = foto;
    }

    public int getId() {
        return id;
    }
}
