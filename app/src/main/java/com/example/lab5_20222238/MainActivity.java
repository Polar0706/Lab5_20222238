package com.example.lab5_20222238;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.lab5_20222238.activities.ConfiguracionesActivity;
import com.example.lab5_20222238.activities.CursosActivity;
import com.example.lab5_20222238.databinding.ActivityMainBinding;
import com.example.lab5_20222238.utils.NotificationHelper;
import com.example.lab5_20222238.utils.StorageManager;
import com.example.lab5_20222238.workers.MotivationalNotificationWorker;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private StorageManager storageManager;
    private ActivityResultLauncher<Intent> configuracionesLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageManager = new StorageManager(this);

        NotificationHelper.crearCanalesNotificacion(this);

        solicitarPermisosNotificaciones();

        configuracionesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        cargarDatosUsuario();
                    }
                }
        );

        if (storageManager.esPrimeraEjecucion()) {
            abrirConfiguraciones();
        } else {
            cargarDatosUsuario();
            programarNotificacionMotivacional();
        }

        binding.btnVerCursos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CursosActivity.class);
            startActivity(intent);
        });

        binding.btnConfiguraciones.setOnClickListener(v -> abrirConfiguraciones());
    }

    private void cargarDatosUsuario() {
        String nombre = storageManager.obtenerNombreUsuario();
        String mensaje = storageManager.obtenerMensajeMotivacional();
        String rutaImagen = storageManager.obtenerRutaImagenPerfil();

        binding.textSaludo.setText("¡Hola, " + nombre + "!");
        binding.textMensajeMotivacional.setText(mensaje);

        if (rutaImagen != null) {
            File imgFile = new File(rutaImagen);
            if (imgFile.exists()) {
                binding.imagenPerfil.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            }
        }
    }

    private void abrirConfiguraciones() {
        Intent intent = new Intent(MainActivity.this, ConfiguracionesActivity.class);
        configuracionesLauncher.launch(intent);
    }

    private void programarNotificacionMotivacional() {
        int horas = storageManager.obtenerHorasNotificacionMotivacional();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                MotivationalNotificationWorker.class,
                horas,
                TimeUnit.HOURS
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "motivational_notification",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
        );
    }

    private void solicitarPermisosNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }
    }
}