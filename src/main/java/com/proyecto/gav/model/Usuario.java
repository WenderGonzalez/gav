package com.proyecto.gav.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    private String nombreCompleto;
    private String apellidosCompletos;

    @Indexed(unique = true)
    private String nombreUsuario;
    private String contrasena;

    @Indexed(unique = true)
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;

    @Indexed(unique = true)
    private String numeroDocumento;
    private TipoDocumento tipoDocumento;

    @Indexed(unique = true)
    private String telefono;

    @Indexed
    private Rol rol;
    private Sexo sexo;
    private int edad;

    // Atributos para CONDUCTOR
    @Indexed
    private Boolean disponibilidad;

    private String licencia;

    private TipoLicencia tipoLicencia;

    @DBRef(lazy = false)
    private Automovil automovil;

    public enum TipoDocumento {
        CEDULA,
        PASAPORTE,
        CEDULA_EXTRANJERIA
    }

    public enum TipoLicencia {
        C1, C2, C3
    }

    public enum Rol {
        ADMIN, CONDUCTOR, CLIENTE
    }

    public enum Sexo {
        MASCULINO, FEMENINO
    }

    public Usuario() {
    }

    public Usuario(String id, String nombreCompleto, String apellidosCompletos, String nombreUsuario, String contrasena, String email, Date fechaNacimiento, String numeroDocumento, TipoDocumento tipoDocumento, String telefono, Rol rol, Sexo sexo, int edad, Boolean disponibilidad, String licencia, TipoLicencia tipoLicencia, Automovil automovil) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.apellidosCompletos = apellidosCompletos;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.numeroDocumento = numeroDocumento;
        this.tipoDocumento = tipoDocumento;
        this.telefono = telefono;
        this.rol = rol;
        this.sexo = sexo;
        this.edad = edad;
        this.disponibilidad = disponibilidad;
        this.licencia = licencia;
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

    public String getApellidosCompletos() {
        return apellidosCompletos;
    }

    public void setApellidosCompletos(String apellidosCompletos) {
        this.apellidosCompletos = apellidosCompletos;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public Boolean getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public TipoLicencia getTipoLicencia() {
        return tipoLicencia;
    }

    public void setTipoLicencia(TipoLicencia tipoLicencia) {
        this.tipoLicencia = tipoLicencia;
    }

    public Automovil getAutomovil() {
        return automovil;
    }

    public void setAutomovil(Automovil automovil) {
        this.automovil = automovil;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(getId(), usuario.getId()) && Objects.equals(getNombreUsuario(), usuario.getNombreUsuario()) && Objects.equals(getEmail(), usuario.getEmail()) && Objects.equals(getNumeroDocumento(), usuario.getNumeroDocumento()) && Objects.equals(getTelefono(), usuario.getTelefono()) && Objects.equals(getLicencia(), usuario.getLicencia()) && Objects.equals(getAutomovil(), usuario.getAutomovil());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNombreUsuario(), getEmail(), getNumeroDocumento(), getTelefono(), getLicencia(), getAutomovil());
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", apellidosCompletos='" + apellidosCompletos + '\'' +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", email='" + email + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", telefono='" + telefono + '\'' +
                ", rol=" + rol +
                ", sexo=" + sexo +
                ", edad=" + edad +
                ", disponibilidad=" + disponibilidad +
                ", licencia='" + licencia + '\'' +
                ", tipoLicencia=" + tipoLicencia +
                ", automovil=" + automovil +
                '}';
    }
}


