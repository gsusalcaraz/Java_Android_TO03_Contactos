package com.example.to03_contactos.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

// Clase que hereda de SQLiteOpenHelper
public class ContactosDB extends SQLiteOpenHelper {

    private ArrayList<Contacto> lista;
    public ArrayList<Contacto> getLista() {
        return lista;
    }

    public ContactosDB(@Nullable Context context) {
        super(context, DbUtils.DATABASE_NAME, null, DbUtils.DATABASE_VERSION);
        lista = new ArrayList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DbUtils.CREAR_TABLA_CONTACTOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DbUtils.DROP_TABLA_CONTACTOS);
        onCreate(sqLiteDatabase);
    }

    /**
     * Método que carga todos los contactos de la base de datos
     */
    public boolean cargar() {

        lista.clear();

        SQLiteDatabase db = this.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(DbUtils.SELECT_CONTACTOS, null);
            if (cursor.moveToFirst()) {
                do {

                    Contacto item = new Contacto(cursor.getInt(cursor.getColumnIndexOrThrow(DbUtils.CAMPO_ID))
                            , cursor.getString(cursor.getColumnIndexOrThrow(DbUtils.CAMPO_NOMBRE))
                            , cursor.getString(cursor.getColumnIndexOrThrow(DbUtils.CAMPO_TELEFONO))
                            , cursor.getString(cursor.getColumnIndexOrThrow(DbUtils.CAMPO_EMAIL))
                            , cursor.getInt(cursor.getColumnIndexOrThrow(DbUtils.CAMPO_IMAGEN)));
                    lista.add(item);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        return !lista.isEmpty();
    }

    /**
     * Método que carga un contacto mediante el id
     * @param id
     */
    public Contacto cargar(int id){
        Contacto item = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db.isOpen()) {

            String[] columns = new String[]{
                    DbUtils.CAMPO_ID,
                    DbUtils.CAMPO_NOMBRE,
                    DbUtils.CAMPO_TELEFONO,
                    DbUtils.CAMPO_EMAIL,
                    DbUtils.CAMPO_IMAGEN
            };
            String where = DbUtils.CAMPO_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(id)};

            Cursor cursor = db.query(DbUtils.TABLA_CONTACTOS
                    , columns , where , whereArgs
                    , null, null, null);

            if (cursor.moveToFirst()) {

                item = new Contacto(cursor.getInt(cursor.getColumnIndexOrThrow(DbUtils.CAMPO_ID))
                        , cursor.getString(cursor.getColumnIndexOrThrow(DbUtils.CAMPO_NOMBRE))
                        , cursor.getString(cursor.getColumnIndexOrThrow(DbUtils.CAMPO_TELEFONO))
                        , cursor.getString(cursor.getColumnIndexOrThrow(DbUtils.CAMPO_EMAIL))
                        , cursor.getInt(cursor.getColumnIndexOrThrow(DbUtils.CAMPO_IMAGEN)));
            }
            cursor.close();
            db.close();
        }
        return item;
    }

    /**
     * Método que inserta un contacto en la base de datos
     * @param item
     */
    public long insertar(@NonNull Contacto item) {

        ContentValues valores = new ContentValues();
        valores.put(DbUtils.CAMPO_NOMBRE, item.getNombre());
        valores.put(DbUtils.CAMPO_TELEFONO, item.getTelefono());
        valores.put(DbUtils.CAMPO_EMAIL, item.getEmail());
        valores.put(DbUtils.CAMPO_IMAGEN, item.getImagen());

        SQLiteDatabase db = this.getWritableDatabase();
        long resultado = db.insert(DbUtils.TABLA_CONTACTOS, null, valores);
        db.close();

        return resultado;
    }

    /**
     * Método que actualiza un contacto en la base de datos
     * @params item, id
     */
    public int actualizar(@NonNull Contacto item, int id){
        ContentValues valores = new ContentValues();
        valores.put(DbUtils.CAMPO_NOMBRE, item.getNombre());
        valores.put(DbUtils.CAMPO_TELEFONO, item.getTelefono());
        valores.put(DbUtils.CAMPO_EMAIL, item.getEmail());
        valores.put(DbUtils.CAMPO_IMAGEN, item.getImagen());

        String where = DbUtils.CAMPO_ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(item.getId())};

        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.update(DbUtils.TABLA_CONTACTOS,  valores, where, whereArgs);
        db.close();

        return resultado;
    }

    /**
     * Método que elimina un contacto de la base de datos y lista
     * @params id, posicion
     */
    public int eliminar(int id, int posicion) {
        String where = DbUtils.CAMPO_ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(id)};

        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete(DbUtils.TABLA_CONTACTOS, where, whereArgs);
        db.close();
        // Fuerzo borrar el elemento de la lista
        // Si no, lo elimina y lo vuelve a cargar
        lista.remove(posicion);
        return resultado;
    }

    /**
     * Método que elimina todos los contactos de la base de datos
     */
    public void eliminarTodos(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DbUtils.DELETE_CONTACTOS);
        db.close();
        lista.clear();
    }
}
