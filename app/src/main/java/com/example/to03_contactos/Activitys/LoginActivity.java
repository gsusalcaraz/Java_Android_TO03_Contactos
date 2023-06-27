package com.example.to03_contactos.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.to03_contactos.R;

public class LoginActivity extends AppCompatActivity {

    private Button btLogin;
    private EditText etUsuario, etPassword;
    private CheckBox cbPreferencias;

    private String user = "admin";
    private String pass = "admin";

    // Declarar las preferencias
    private SharedPreferences preferencias;
    private SharedPreferences.Editor editor;
    private String llave = "sesion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarElementos();

        if (revisarSesion()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Usuario logueado: "+user, Toast.LENGTH_SHORT).show();
        }

        botonLogin();
    }

    private void botonLogin() {
        // Botón login
        btLogin.setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString();
            String password = etPassword.getText().toString();

            // Validar usuario y contraseña
            if (usuario.equals(user) && password.equals(pass)) {
                if (cbPreferencias.isChecked()) {
                    editor.putBoolean(llave, true);
                    editor.apply();
                }

                // Llamar a la actividad principal
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();

            }else{
                Toast.makeText(this, "admin / admin", Toast.LENGTH_SHORT).show();
                etUsuario.setText("admin");
                etPassword.setText("admin");
            }
        });
    }
    private void inicializarElementos() {
        btLogin = findViewById(R.id.btLogin);
        etUsuario = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPass);
        cbPreferencias = findViewById(R.id.cbPreferencias);

        preferencias = this.getSharedPreferences("sesiones", Context.MODE_PRIVATE);
        editor = preferencias.edit();
    }
    private boolean revisarSesion() {
        return this.preferencias.getBoolean(llave, false);
    }
}