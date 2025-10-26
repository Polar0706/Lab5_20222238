package com.example.lab5_20222238.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.lab5_20222238.models.Curso;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorageManager {
    private static final String PREFS_NAME = "GestorEstudioPrefs";
    private static final String KEY_NOMBRE_USUARIO = "nombre_usuario";
    private static final String KEY_MENSAJE_MOTIVACIONAL = "mensaje_motivacional";
    private static final String KEY_CURSOS = "cursos_list";
    private static final String KEY_IMAGEN_PERFIL = "imagen_perfil_path";
    private static final String KEY_HORAS_NOTIF_MOTIVACIONAL = "horas_notif_motivacional";

    private SharedPreferences prefs;
    private Gson gson;
    private Context context;

    public StorageManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void guardarNombreUsuario(String nombre) {
        prefs.edit().putString(KEY_NOMBRE_USUARIO, nombre).apply();
    }

    public String obtenerNombreUsuario() {
        return prefs.getString(KEY_NOMBRE_USUARIO, "");
    }

    public void guardarMensajeMotivacional(String mensaje) {
        prefs.edit().putString(KEY_MENSAJE_MOTIVACIONAL, mensaje).apply();
    }

    public String obtenerMensajeMotivacional() {
        return prefs.getString(KEY_MENSAJE_MOTIVACIONAL, "Hoy es un gran día para aprender");
    }

    public void guardarHorasNotificacionMotivacional(int horas) {
        prefs.edit().putInt(KEY_HORAS_NOTIF_MOTIVACIONAL, horas).apply();
    }

    public int obtenerHorasNotificacionMotivacional() {
        return prefs.getInt(KEY_HORAS_NOTIF_MOTIVACIONAL, 24);
    }

    public void guardarCursos(List<Curso> cursos) {
        String json = gson.toJson(cursos);
        prefs.edit().putString(KEY_CURSOS, json).apply();
    }

    // IA utilizada: claude sonnet 2 /Prompt: Como guardar y recuperar una lista de objetos personalizados en SharedPreferences usando Gson /Comentario: La IA me ayudó a entender el uso de TypeToken para deserializar listas genéricas, ya que sin esto Gson no puede saber el tipo exacto de la lista
    public List<Curso> obtenerCursos() {
        String json = prefs.getString(KEY_CURSOS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        
        Type type = new TypeToken<List<Curso>>(){}.getType();
        return gson.fromJson(json, type);
    }
    
    public void agregarCurso(Curso curso) {
        List<Curso> cursos = obtenerCursos();
        cursos.add(curso);
        guardarCursos(cursos);
    }

    public void eliminarCurso(String cursoId) {
        List<Curso> cursos = obtenerCursos();
        cursos.removeIf(c -> c.getId().equals(cursoId));
        guardarCursos(cursos);
    }

    public String guardarImagenPerfil(Uri imageUri) {
        try {
            File directory = new File(context.getFilesDir(), "profile_images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File imageFile = new File(directory, "profile_image.jpg");
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            String path = imageFile.getAbsolutePath();
            prefs.edit().putString(KEY_IMAGEN_PERFIL, path).apply();
            return path;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String obtenerRutaImagenPerfil() {
        return prefs.getString(KEY_IMAGEN_PERFIL, null);
    }

    public boolean esPrimeraEjecucion() {
        return obtenerNombreUsuario().isEmpty();
    }
}
