package com.proyecto.gav.DTO;

import com.proyecto.gav.model.Automovil;
import org.springframework.data.mongodb.core.mapping.Field;

public class ConductorDisponibleDTO {

    private String id;
    private String nombreCompleto;
    private String tipoLicencia;

    @Field("automovilDoc")
    private Automovil automovil;

    public ConductorDisponibleDTO() {
    }

    public ConductorDisponibleDTO(String id, String nombreCompleto, String tipoLicencia, Automovil automovil) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.tipoLicencia = tipoLicencia;
        this.automovil = automovil;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTipoLicencia() {
        return tipoLicencia;
    }

    public void setTipoLicencia(String tipoLicencia) {
        this.tipoLicencia = tipoLicencia;
    }

    public Automovil getAutomovil() {
        return automovil;
    }

    public void setAutomovil(Automovil automovil) {
        this.automovil = automovil;
    }
}
