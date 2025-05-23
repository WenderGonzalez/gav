package com.proyecto.gav.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
@Table(name = "locacion")
public class Locacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreLugar;
    private String descripcion;
    private double latitud;
    private double longitud;

    @Column(nullable = true)
    private BigDecimal precio;


    public Locacion() {
    }

    public Locacion(Long id, String nombreLugar, String descripcion, double latitud, double longitud, BigDecimal precio) {
        this.id = id;
        this.nombreLugar = nombreLugar;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.precio = precio;
    }

    public Locacion(String nombreLugar, String descripcion, double latitud, double longitud, BigDecimal precio) {
        this.nombreLugar = nombreLugar;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreLugar() {
        return nombreLugar;
    }

    public void setNombreLugar(String nombreLugar) {
        this.nombreLugar = nombreLugar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Locacion{" +
                "id=" + id +
                ", nombreLugar='" + nombreLugar + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", precio=" + precio +
                '}';
    }
}