package com.suplidora.sistemas.sisago.AccesoDatos;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.suplidora.sistemas.sisago.Auxiliar.variables_publicas;
import com.suplidora.sistemas.sisago.Entidades.Usuario;

public class UsuariosHelper {

    private SQLiteDatabase database;

    public  UsuariosHelper(SQLiteDatabase db){
        database = db;
    }
    public boolean GuardarUsuario(String Codigo, String nombre, String Usuario,
                                   String Contrasenia, String Tipo, String Ruta,
                                      String Canal,String TasaCambio,String RutaForanea,String FechaActualiza) {

        long rows =0;
        ContentValues contentValues = new ContentValues();
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Codigo, Codigo);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Nombre, nombre);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Usuario, Usuario);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Contrasenia, Contrasenia);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Tipo, Tipo);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Ruta, Ruta);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Canal, Canal);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_TasaCambio, TasaCambio);
        contentValues.put(variables_publicas.USUARIOS_COLUMN_RutaForanea, RutaForanea);
        contentValues.put(variables_publicas.USUARIOS_COLUMN_FechaActualiza, FechaActualiza);
         long inserted=database.insert(variables_publicas.TABLE_USUARIOS, null, contentValues);
        if(inserted!=-1)
            return true;
        else
            return false;
    }
    public Usuario BuscarUsuarios(String Usuario,String Contrasenia) {
        Usuario usuario=null;
        String selectQuery="SELECT * FROM " + variables_publicas.TABLE_USUARIOS
                + " WHERE UPPER("+variables_publicas.USUARIOS_COLUMN_Usuario +") = UPPER('"+Usuario+"') AND "+ variables_publicas.USUARIOS_COLUMN_Contrasenia+" = '"+Contrasenia+"'";
        Cursor c= database.rawQuery(selectQuery , null);
        if (c.moveToFirst()) {
            do {
                usuario = (new Usuario(c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Codigo)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Nombre)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Usuario)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Contrasenia)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Tipo)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Ruta)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Canal)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_TasaCambio)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_RutaForanea)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_FechaActualiza))

                ));
            } while (c.moveToNext());
        }
        c.close();
        return usuario;
    }
    public Usuario BuscarUltimoUsuario() {
        Usuario usuario=null;
        String selectQuery="SELECT * FROM " + variables_publicas.TABLE_USUARIOS+" LIMIT 1";

        Cursor c= database.rawQuery(selectQuery , null);
        if (c.moveToFirst()) {
            do {
                usuario = (new Usuario(c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Codigo)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Nombre)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Usuario)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Contrasenia)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Tipo)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Ruta)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Canal)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_TasaCambio)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_RutaForanea)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_FechaActualiza))

                ));
            } while (c.moveToNext());
        }
        c.close();
        return usuario;
    }
    public  void EliminaUsuarios() {
        database.execSQL("DELETE FROM "+variables_publicas.TABLE_USUARIOS+";");
        Log.d("Usuario_elimina", "Datos eliminados");
    }


    }
