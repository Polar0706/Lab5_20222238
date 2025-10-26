package com.example.lab5_20222238.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.lab5_20222238.utils.NotificationHelper;
import com.example.lab5_20222238.utils.StorageManager;

public class MotivationalNotificationWorker extends Worker {

    public MotivationalNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        StorageManager storageManager = new StorageManager(getApplicationContext());
        String mensaje = storageManager.obtenerMensajeMotivacional();
        
        NotificationHelper.mostrarNotificacionMotivacional(getApplicationContext(), mensaje);
        
        return Result.success();
    }
}
