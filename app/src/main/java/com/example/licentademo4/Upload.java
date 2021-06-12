package com.example.licentademo4;

public class Upload {
    private String rezultat;
    private String URLImagine;

    public Upload() {
    }

    public Upload(String rezultat, String URLImagine) {
        this.rezultat = rezultat;
        this.URLImagine = URLImagine;
    }

    public String getRezultat() {
        return rezultat;
    }

    public void setRezultat(String rezultat) {
        this.rezultat = rezultat;
    }

    public String getURLImagine() {
        return URLImagine;
    }

    public void setURLImagine(String URLImagine) {
        this.URLImagine = URLImagine;
    }
}
