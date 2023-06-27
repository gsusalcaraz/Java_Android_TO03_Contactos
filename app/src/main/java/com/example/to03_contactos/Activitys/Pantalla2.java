package com.example.to03_contactos.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.to03_contactos.Models.Contacto;
import com.example.to03_contactos.Models.ContactosDB;
import com.example.to03_contactos.Models.Validar;
import com.example.to03_contactos.R;

public class Pantalla2 extends AppCompatActivity {

    private ContactosDB contactosDB;
    private Contacto contacto;
    private Button btGuardar;
    private EditText etNombre, etTelefono, etEmail;
    //ImageView ivContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla2);

        // Obtiene parámetro enviado desde la actividad principal
        Intent intent = getIntent();
        Boolean message = intent.getBooleanExtra("editar", true);

        contactosDB = new ContactosDB(this);

        inicializarElementos();

        if (message){
            setTitle("Editar contacto");
            Bundle bundle = getIntent().getExtras();
            contacto = (Contacto) bundle.getSerializable("contacto");
            final int id = contacto.getId();
            contactosDB.cargar(contacto.getId());

            etNombre.setText(contacto.getNombre());
            etTelefono.setText(contacto.getTelefono());
            etEmail.setText(contacto.getEmail());

            btGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String nombre = etNombre.getText().toString();
                    String telefono = etTelefono.getText().toString();
                    String email = etEmail.getText().toString();

                    if (!Validar.validarNombre(nombre)) {
                        Toast.makeText(getApplicationContext(), "El nombre no es válido", Toast.LENGTH_SHORT).show();
                    } else if (!Validar.validarTelefono(telefono)) {
                        Toast.makeText(getApplicationContext(), "El teléfono no es válido", Toast.LENGTH_SHORT).show();
                    } else if (!Validar.validarEmail(email)) {
                        Toast.makeText(getApplicationContext(), "El email no es válido", Toast.LENGTH_SHORT).show();
                    } else {
                        contactosDB.actualizar(new Contacto(id, nombre,telefono, email, R.drawable.contacto), id);
                        Toast.makeText(getApplicationContext(), "Contacto actualizado", Toast.LENGTH_SHORT).show();
                        //  myAdapter.notifyItemInserted();
                        finish();
                    }
                }
            });
        } else {
            setTitle("Nuevo contacto");
            btGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String nombre = etNombre.getText().toString();
                    String telefono = etTelefono.getText().toString();
                    String email = etEmail.getText().toString();

                    // Validar para guardar contactos válidos
                    if (!Validar.validarNombre(nombre)) {
                        Toast.makeText(getApplicationContext(), "El nombre no es válido", Toast.LENGTH_SHORT).show();
                    } else if (!Validar.validarTelefono(telefono)) {
                        Toast.makeText(getApplicationContext(), "El teléfono no es válido", Toast.LENGTH_SHORT).show();
                    } else if (!Validar.validarEmail(email)) {
                        Toast.makeText(getApplicationContext(), "El email no es válido", Toast.LENGTH_SHORT).show();
                    } else {
                        contactosDB.insertar(new Contacto(-1, nombre,telefono, email, R.drawable.contacto));
                        Toast.makeText(getApplicationContext(), "Contacto guardado", Toast.LENGTH_SHORT).show();
                        //  myAdapter.notifyItemInserted();
                        finish();
                    }
                }
            });
        }

    }

    private void inicializarElementos(){
        btGuardar = findViewById(R.id.btGuardar);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etEmail = findViewById(R.id.etEmail);
    }
}