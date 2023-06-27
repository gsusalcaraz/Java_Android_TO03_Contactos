package com.example.to03_contactos.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.to03_contactos.Interfaces.RecyclerViewItemClick;
import com.example.to03_contactos.Models.Contacto;
import com.example.to03_contactos.Adapters.ContactosAdapter;
import com.example.to03_contactos.Models.ContactosDB;
import com.example.to03_contactos.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewItemClick {

    private RecyclerView recyclerView;
    private ContactosAdapter myAdapter;
    private ContactosDB contactosDB;
    private FloatingActionButton fabAdd;
    private Boolean permisosLectura;

    private SharedPreferences preferencias;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar elementos
        inicializarElementos();

        // Obtener permisos
        getPermissionToReadUserContacts();

        // Asocia el LayoutManager al recyclerView
        LinearLayoutManager lmg = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lmg);

        //Carga en memoria los datos que va a representar en el recyclerView
        contactosDB.cargar();

        // Crea el adaptador, le pasa los datos e implementa el itemclick
        // Rellena el adapater
        myAdapter = new ContactosAdapter((ArrayList<Contacto>) contactosDB.getLista(), this);

        //Asocia el adaptador al recycler
        recyclerView.setAdapter(myAdapter);

        // Botón flotante de añadir contacto
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Pantalla2.class);
                i.putExtra("editar", false);
                startActivity(i);
            }
        });
    }

    // Inicializar elementos
    private void inicializarElementos() {
        preferencias = this.getSharedPreferences("sesiones", Context.MODE_PRIVATE);
        editor = preferencias.edit();

        // Captura el recyclerView del layout
        recyclerView = findViewById(R.id.contenedorContactos);

        fabAdd = findViewById(R.id.fabAdd);
        contactosDB = new ContactosDB(this);
    }

    // Fuerza a que se recargue la lista al volver a la actividad
    protected void onResume() {
        super.onResume();
        contactosDB.cargar();
        myAdapter.notifyDataSetChanged();
    }

    // Crear submenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Opciones del submenu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.importar:
                if (permisosLectura){
                    importarContactos();
                } else {
                    Toast.makeText(this, "No tienes permisos para importar contactos", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Revisa los permisos de la APP", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.exportar:
                exportarContactos();
                return true;
            case R.id.exportarsd:
                exportarContactosExterna();
                return true;
            case R.id.eliminar:
                eliminarContactos();
                return true;
            case R.id.cerrar:
                cerrarSesion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRecyclerviewMenuitemClick(MenuItem menuItem, int posicion, Contacto contacto) {
        switch (menuItem.getItemId()) {
            case R.id.popup_editar:
                editarContacto(contacto);
                break;
            case R.id.popup_eliminar:
                borrarContacto(contacto, posicion);
                break;
        }
    }

    /**
     * Método llamado para editar en el menú popup
     * @param contacto
     */
    public void editarContacto(Contacto contacto) {
        Contacto c = contactosDB.cargar(contacto.getId());
        Intent i = new Intent(this, Pantalla2.class);
        Bundle b = new Bundle();
        b.putSerializable("contacto", c);
        i.putExtra("editar", true);
        i.putExtras(b);
        startActivity(i);
    }

    /**
     * Método llamado para borrar en el menú popup
     * @param contacto
     * @param posicion
     */
    public void borrarContacto(Contacto contacto, int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(R.string.atencion);
        builder.setMessage(R.string.pregunta_individual);
        builder.setPositiveButton(R.string.confirmar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Borro registro del ArrayList
                        contactosDB.eliminar(contacto.getId(), posicion);
                        // Borro registro de la vista
                        myAdapter.notifyItemRemoved(posicion);
                        Toast.makeText(MainActivity.this, R.string.registroeliminado, Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Contacto no eliminado", Toast.LENGTH_SHORT).show();
            }
        });

        // Mostrar mensaje de confirmación
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Método que importa los contactos del teléfono
     */
    private void importarContactos() {
        String email = "prueba@prueba.es";
        int numContactos = 0;

        //Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            @SuppressLint("Range") String nombre = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String telefono = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //@SuppressLint("Range") String email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            //@SuppressLint("Range") String email = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            //@SuppressLint("Range") String email = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));

            // Limpia el teléfono de espacios, guiones y paréntesis
            telefono = telefono.replaceAll("\\D+", "");
            contactosDB.insertar(new Contacto(-1, nombre, telefono, email, R.drawable.contacto));
            numContactos++;
        }
        phones.close();
        Toast.makeText(this, "Contactos importados: "+numContactos, Toast.LENGTH_SHORT).show();
        onResume();
    }

    /**
     * Método que elimina todos los contactos
     */
    public void eliminarContactos(){
        // Mensaje de confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(R.string.atencion);
        builder.setMessage(R.string.pregunta);
        builder.setPositiveButton(R.string.confirmar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contactosDB.eliminarTodos();
                        onResume();
                        Toast.makeText(MainActivity.this, R.string.registroseliminados, Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Contactos no eliminados", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Método que exporta los contactos a un json
     */
    private void exportarContactos(){
        String listaContactos = new Gson().toJson(contactosDB.getLista());
        try {
            FileOutputStream fos = openFileOutput("contactos.json", Context.MODE_PRIVATE);
            fos.write(listaContactos.getBytes());
            fos.close();
            Toast.makeText(this, "Contactos exportados", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que exporta los contactos a un json en memoria externa
     */
    private void exportarContactosExterna() {
        String listaContactos = new Gson().toJson(contactosDB.getLista());
        String estado = Environment.getExternalStorageState();
        if (estado.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(getExternalFilesDir(null), "listaContactos.json");
            OutputStreamWriter fout = null;
            try {
                fout = new OutputStreamWriter(new FileOutputStream(file));
                fout.write(listaContactos);
                Toast.makeText(this, "Contactos exportados a SD", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fout != null) {
                        fout.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

     /**
     * Método para cerrar la sesión actual
     */
    private void cerrarSesion(){
        editor.putBoolean("sesion", false);
        editor.apply();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    // PERMITIR PERMISOS
    /**
     * Identificador para la petición de permisos
     */
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    /**
     * Se llama cuando se requiere que la aplicación lea los contactos
     */
    public void getPermissionToReadUserContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {
            }
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        } else
            permisosLectura = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos de lectura de contactos adquiridos", Toast.LENGTH_SHORT).show();
                permisosLectura = true;
            } else {
                Toast.makeText(this, "Permisos de lectura de contactos no adquiridos", Toast.LENGTH_SHORT).show();
                permisosLectura = false;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onItemClick(@NonNull Contacto contacto) {
        //datosContacto(contacto);
        Toast.makeText(this, "Haz clic en el menú para ver las opciones :)", Toast.LENGTH_SHORT).show();
    }


}