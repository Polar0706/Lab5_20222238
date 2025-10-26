package com.example.lab5_20222238.models;

import java.io.Serializable;

public class Curso implements Serializable {
    private String id;
    private String nombre;
    private String categoria;
    private int frecuenciaDias;
    private long proximaSesionTimestamp;

    public Curso() {
    }

    public Curso(String id, String nombre, String categoria, int frecuenciaDias, long proximaSesionTimestamp) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.frecuenciaDias = frecuenciaDias;
        this.proximaSesionTimestamp = proximaSesionTimestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getFrecuenciaDias() {
        return frecuenciaDias;
    }

    public void setFrecuenciaDias(int frecuenciaDias) {
        this.frecuenciaDias = frecuenciaDias;
    }

    public long getProximaSesionTimestamp() {
        return proximaSesionTimestamp;
    }

    public void setProximaSesionTimestamp(long proximaSesionTimestamp) {
        this.proximaSesionTimestamp = proximaSesionTimestamp;
    }
}
