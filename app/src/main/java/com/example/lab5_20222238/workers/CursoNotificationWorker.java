package com.example.lab5_20222238.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.lab5_20222238.models.Curso;
import com.example.lab5_20222238.utils.NotificationHelper;
import com.example.lab5_20222238.utils.StorageManager;

import java.util.List;

public class CursoNotificationWorker extends Worker {

    public CursoNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String cursoId = getInputData().getString("curso_id");
        
        if (cursoId != null) {
            StorageManager storageManager = new StorageManager(getApplicationContext());
            List<Curso> cursos = storageManager.obtenerCursos();
            
            for (Curso curso : cursos) {
                if (curso.getId().equals(cursoId)) {
                    String accionSugerida = obtenerAccionSugerida(curso.getCategoria());
                    NotificationHelper.mostrarNotificacionCurso(
                            getApplicationContext(),
                            curso.getNombre(),
                            accionSugerida,
                            curso.getCategoria()
                    );
                    break;
                }
            }
        }
        
        return Result.success();
    }

    private String obtenerAccionSugerida(String categoria) {
        if (categoria == null) return "Revisa tu material de estudio";
        
        switch (categoria.toLowerCase()) {
            case "teorico":
            case "teórico":
                return "Revisar apuntes y lecturas del curso";
            case "laboratorio":
                return "Completar práctica de laboratorio";
            case "electivo":
                return "Revisar material del curso electivo";
            default:
                return "Revisar material de estudio";
        }
    }
}
