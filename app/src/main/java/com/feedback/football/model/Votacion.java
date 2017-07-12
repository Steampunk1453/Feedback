package com.feedback.football.model;


public class Votacion {

    String id;
    String partido;
    String minuto;
    String jugada;
    int ok;
    int ko;

    public Votacion() {
    }

    public Votacion(String id, String partido, String minuto, String jugada, int ok, int ko) {
        this.id = id;
        this.partido = partido;
        this.minuto = minuto;
        this.jugada = jugada;
        this.ok = ok;
        this.ko = ko;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartido() {
        return partido;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }

    public String getMinuto() {
        return minuto;
    }

    public void setMinuto(String minuto) {
        this.minuto = minuto;
    }

    public String getJugada() {
        return jugada;
    }

    public void setJugada(String jugada) {
        this.jugada = jugada;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public int getKo() {
        return ko;
    }

    public void setKo(int ko) {
        this.ko = ko;
    }
}
