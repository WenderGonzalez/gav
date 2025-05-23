package com.proyecto.gav.model;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Document(collection = "viajes")
public class Viaje {

    @Id
    private String id;

    private int cantidadPasajeros;

    private Long destinoId;
    private Long origenId;

    private EstadoViaje estadoViaje;

    @DBRef(lazy = false)
    private Usuario cliente;

    @DBRef(lazy = false)
    private Usuario conductor;

    private BigDecimal precioFinal;

    @DBRef(lazy = false)
    private Automovil automovil;

    private Date fechaSolicitud;
    private Date fechaInicio;
    private Date fechaCancelacion;
    private Date fechaFinalizacion;

    public enum EstadoViaje {
        SOLICITADO,
        ASIGNADO,
        EN_CURSO,
        CANCELADO,
        FINALIZADO
    }

    public Viaje() {
    }

    public Viaje(String id, int cantidadPasajeros, Long destinoId, Long origenId, EstadoViaje estadoViaje, Usuario cliente, Usuario conductor, BigDecimal precioFinal, Automovil automovil, Date fechaSolicitud, Date fechaInicio, Date fechaCancelacion, Date fechaFinalizacion) {
        this.id = id;
        this.cantidadPasajeros = cantidadPasajeros;
        this.destinoId = destinoId;
        this.origenId = origenId;
        this.estadoViaje = estadoViaje;
        this.cliente = cliente;
        this.conductor = conductor;
        this.precioFinal = precioFinal;
        this.automovil = automovil;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaInicio = fechaInicio;
        this.fechaCancelacion = fechaCancelacion;
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCantidadPasajeros() {
        return cantidadPasajeros;
    }

    public void setCantidadPasajeros(int cantidadPasajeros) {
        this.cantidadPasajeros = cantidadPasajeros;
    }

    public Long getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(Long destinoId) {
        this.destinoId = destinoId;
    }

    public Long getOrigenId() {
        return origenId;
    }

    public void setOrigenId(Long origenId) {
        this.origenId = origenId;
    }

    public EstadoViaje getEstadoViaje() {
        return estadoViaje;
    }

    public void setEstadoViaje(EstadoViaje estadoViaje) {
        this.estadoViaje = estadoViaje;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public Usuario getConductor() {
        return conductor;
    }

    public void setConductor(Usuario conductor) {
        this.conductor = conductor;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public Automovil getAutomovil() {
        return automovil;
    }

    public void setAutomovil(Automovil automovil) {
        this.automovil = automovil;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(Date fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public Date getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Viaje viaje = (Viaje) o;
        return Objects.equals(getId(), viaje.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Viaje{" +
                "id='" + id + '\'' +
                ", cantidadPasajeros=" + cantidadPasajeros +
                ", destinoId=" + destinoId +
                ", origenId=" + origenId +
                ", estadoViaje=" + estadoViaje +
                ", cliente=" + cliente +
                ", conductor=" + conductor +
                ", precioFinal=" + precioFinal +
                ", automovil=" + automovil +
                ", fechaSolicitud=" + fechaSolicitud +
                ", fechaInicio=" + fechaInicio +
                ", fechaCancelacion=" + fechaCancelacion +
                ", fechaFinalizacion=" + fechaFinalizacion +
                '}';
    }
}