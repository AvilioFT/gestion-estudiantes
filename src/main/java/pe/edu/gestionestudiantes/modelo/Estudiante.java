package pe.edu.gestionestudiantes.modelo;

import java.io.Serializable;

public class Estudiante implements Serializable {

    private Long id;
    private String codigo;
    private String dni;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String estado;

    public Estudiante() {
        this.estado = "ACTIVO";
    }

    // Constructor para copiar un estudiante
    public Estudiante(Estudiante otro) {
        this.id = otro.id;
        this.codigo = otro.codigo;
        this.dni = otro.dni;
        this.nombres = otro.nombres;
        this.apellidos = otro.apellidos;
        this.correo = otro.correo;
        this.telefono = otro.telefono;
        this.estado = otro.estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}