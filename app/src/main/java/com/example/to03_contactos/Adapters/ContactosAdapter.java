package com.example.to03_contactos.Adapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.to03_contactos.Models.Contacto;
import com.example.to03_contactos.R;
import com.example.to03_contactos.Interfaces.RecyclerViewItemClick;

import java.util.ArrayList;

public class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.ContactosViewHolder> {

    private ArrayList<Contacto> listaContactos;
    private RecyclerViewItemClick recyclerViewItemClick;

    // Constructor
    public ContactosAdapter(ArrayList<Contacto> listaContactos, RecyclerViewItemClick recyclerViewItemClick) {
        this.listaContactos = listaContactos;
        this.recyclerViewItemClick = recyclerViewItemClick;
    }

    // Crea el viewholder
    @NonNull
    @Override
    public ContactosAdapter.ContactosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_row_contactos, parent, false);
        return new ContactosViewHolder(v);
    }

    // Pone los items del recycler view
    @Override
    public void onBindViewHolder(@NonNull ContactosViewHolder holder, int position) {

        holder.nombre.setText(listaContactos.get(position).getNombre());
        holder.telefono.setText(listaContactos.get(position).getTelefono());
        holder.foto.setImageResource(listaContactos.get(position).getImagen());
    }

    // Devuelve la cantidad de items que hay en el recycler view
    @Override
    public int getItemCount() {
        return listaContactos.size();
    }

    // Implementar el ViewHolder
    // Como se muestran los items en el recycler view
    public class ContactosViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView nombre;
        TextView telefono;
        ImageView foto;
        ImageButton menu;

        public ContactosViewHolder(View v) {
            super(v);
            foto = itemView.findViewById(R.id.imageView);
            nombre = itemView.findViewById(R.id.tvNombre);
            telefono = itemView.findViewById(R.id.tvTelefono);
            menu = itemView.findViewById(R.id.ibMenu);

            menu.setOnClickListener(this);
            v.setOnClickListener(view -> recyclerViewItemClick.onItemClick(listaContactos.get(getAdapterPosition())));
        }

        // Método para mostrar el popup menú
        @Override
        public void onClick(View view) {

            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        // Método para gestionar el click del popup menú
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            int posicion = getAdapterPosition();
            recyclerViewItemClick.onRecyclerviewMenuitemClick(item, posicion, listaContactos.get(posicion));
            return false;
        }
    }

}

