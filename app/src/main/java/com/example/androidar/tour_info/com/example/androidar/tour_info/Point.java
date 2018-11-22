package com.example.androidar.tour_info;

import java.net.URL;

public class Point {
    private String coordinateE;
    private String coordinateN;
    private String name;
    private int order;

    private String text;
    private URL[] foto;

    public Point(String coordinateE, String coordinateN, String name, int order) {
        this.coordinateE=coordinateE;
        this.coordinateN=coordinateN;
        this.name=name;
        this.order=order;
    }

    public String getCoorinateE() {
        return coordinateE;
    }

    public String getCoorinateN() {
        return coordinateN;
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
}
