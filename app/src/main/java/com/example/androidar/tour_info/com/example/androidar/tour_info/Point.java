package com.example.androidar.tour_info;

import java.net.URL;

public class Point {
    private String coorinateE;
    private String coorinateN;
    private String name;
    private int order;

    private String text;
    private URL[] foto;

    public Point(String coorinateE, String coorinateN, String name, int order) {
        this.coorinateE=coorinateE;
        this.coorinateN=coorinateN;
        this.name=name;
        this.order=order;
    }

    public String getCoorinateE() {
        return coorinateE;
    }

    public String getCoorinateN() {
        return coorinateN;
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
