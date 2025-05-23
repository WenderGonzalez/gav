package com.proyecto.gav.model;

import jakarta.persistence.Id;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Document(collection = "calificaciones")
public class Calificacion {

    @Id
    private String id;

    @DBRef
    private Viaje viaje;

    @DBRef
    private Usuario calificador;

    @DBRef
    private Usuario calificado;

    private TipoCalificacion tipoCalificacion;

    private int puntuacion;

    private String comentario;

    private Date fechaCalificacion;

    public enum TipoCalificacion {
        CLIENTE_A_CONDUCTOR,
        CONDUCTOR_A_CLIENTE
    }

    public Calificacion() {
    }

    public Calificacion(String id, Date fechaCalificacion, String comentario, int puntuacion, TipoCalificacion tipoCalificacion, Usuario calificado, Usuario calificador, Viaje viaje) {
        this.id = id;
        this.fechaCalificacion = fechaCalificacion;
        this.comentario = comentario;
        this.puntuacion = puntuacion;
        this.tipoCalificacion = tipoCalificacion;
        this.calificado = calificado;
        this.calificador = calificador;
        this.viaje = viaje;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFechaCalificacion() {
        return fechaCalificacion;
    }

    public void setFechaCalificacion(Date fechaCalificacion) {
        this.fechaCalificacion = fechaCalificacion;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public TipoCalificacion getTipoCalificacion() {
        return tipoCalificacion;
    }

    public void setTipoCalificacion(TipoCalificacion tipoCalificacion) {
        this.tipoCalificacion = tipoCalificacion;
    }

    public Usuario getCalificador() {
        return calificador;
    }

    public void setCalificador(Usuario calificador) {
        this.calificador = calificador;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    public Usuario getCalificado() {
        return calificado;
    }

    public void setCalificado(Usuario calificado) {
        this.calificado = calificado;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Calificacion that = (Calificacion) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getCalificador(), that.getCalificador()) && Objects.equals(getCalificado(), that.getCalificado());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCalificador(), getCalificado());
    }

    @Override
    public String toString() {
        return "Calificacion{" +
                "id='" + id + '\'' +
                ", viaje=" + viaje +
                ", calificador=" + calificador +
                ", calificado=" + calificado +
                ", tipoCalificacion=" + tipoCalificacion +
                ", puntuacion=" + puntuacion +
                ", comentario='" + comentario + '\'' +
                ", fechaCalificacion=" + fechaCalificacion +
                '}';
    }
}