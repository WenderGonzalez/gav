package com.proyecto.gav.model;

import jakarta.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "automoviles")
public class    Automovil {

    @Id
    private String id;

    private int capacidadMaxima;
    private String marca;
    private String modelo;
    private String placa;
    private Categoria categoria;

    public enum Categoria {
        SEDAN,
        SUV,
        MINIVAN,
        MINIBUS,
        AUTOBUS,
        BUS

    }

    public Automovil() {
    }

    public Automovil(String id, int capacidadMaxima, String marca, String modelo, String placa, Categoria categoria) {
        this.id = id;
        this.capacidadMaxima = capacidadMaxima;
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
        this.categoria = categoria;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Automovil automovil = (Automovil) o;
        return Objects.equals(getId(), automovil.getId()) && Objects.equals(getPlaca(), automovil.getPlaca());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPlaca());
    }

    @Override
    public String toString() {
        return "Automovil{" +
                "id='" + id + '\'' +
                ", capacidadMaxima=" + capacidadMaxima +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", placa='" + placa + '\'' +
                ", categoria=" + categoria +
                '}';
    }
}