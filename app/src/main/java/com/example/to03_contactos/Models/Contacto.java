package com.example.to03_contactos.Models;

import java.io.Serializable;

// Clase Contacto
public class Contacto implements Serializable {
    private int id;
    private String nombre;
    private String telefono;
    private String email;
    private int imagen;

    // Constructor
    public Contacto(int id, String nombre, String telefono, String email, int imagen) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.imagen = imagen;

    }
    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    // Método equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contacto contacto = (Contacto) o;
        return id == contacto.id;
    }

    // Método hashCode
    @Override
    public int hashCode() {
        return id;
    }

}
