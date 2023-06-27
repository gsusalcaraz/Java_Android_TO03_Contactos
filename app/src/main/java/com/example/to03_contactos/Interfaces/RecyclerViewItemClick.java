package com.example.to03_contactos.Interfaces;

import android.view.MenuItem;

import com.example.to03_contactos.Models.Contacto;

// Interfaz para el click en el RecyclerView
public interface RecyclerViewItemClick {
    void onItemClick(Contacto contacto);
    void onRecyclerviewMenuitemClick(MenuItem menuitem, int posicion, Contacto contacto);
}
