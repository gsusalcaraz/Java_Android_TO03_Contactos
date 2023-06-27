package com.example.to03_contactos.Models;

import android.text.TextUtils;

public class Validar {

    // Que el campo nombre no este vacío
    public static boolean validarNombre(String nombre) {
        if (TextUtils.isEmpty(nombre)) {
            return false;
        } else {
            return true;
        }
    }

    // Que el campo teléfono tenga un teléfono válido
    public static boolean validarTelefono(String telefono) {
        //final String regex = "(\\+34|0034|34)?[ -]*(6|7|8|9)[ -]*([0-9][ -]*){8}";
        final String regex = "(\\+34|0034|34)?[ -]*(6|7|8|9|''')[ -]*([0-9][ -]*){8}";

        if (TextUtils.isEmpty(telefono)) {
            return false;
        } else {
            if (!telefono.matches(regex)) {
                return false;
            }
            return true;
        }
    }

    // Que el campo email tenga un email válido
    public static boolean validarEmail(String email) {
        final String regex = "(?:[^<>()\\[\\].,;:\\s@\"]+(?:\\.[^<>()\\[\\].,;:\\s@\"]+)*|\"[^\\n\"]+\")@(?:[^<>()\\[\\].,;:\\s@\"]+\\.)+[^<>()\\[\\]\\.,;:\\s@\"]{2,63}";

        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            if (!email.matches(regex)) {
                return false;
            }
            return true;
        }
    }
}
