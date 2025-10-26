package com.example.lab5_20222238.activities;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab5_20222238.databinding.ActivityConfiguracionesBinding;
import com.example.lab5_20222238.utils.StorageManager;

import java.io.File;

public class ConfiguracionesActivity extends AppCompatActivity {

    private ActivityConfiguracionesBinding binding;
    private StorageManager storageManager;
    private ActivityResultLauncher<String> imagenLauncher;
    private Uri imagenSeleccionadaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfiguracionesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageManager = new StorageManager(this);

        imagenLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imagenSeleccionadaUri = uri;
                        binding.imagenPerfilConfig.setImageURI(uri);
                    }
                }
        );

        cargarDatosActuales();

        binding.btnSeleccionarImagen.setOnClickListener(v -> {
            imagenLauncher.launch("image/*");
        });

        binding.btnGuardarConfiguraciones.setOnClickListener(v -> guardarConfiguraciones());
    }

    private void cargarDatosActuales() {
        String nombre = storageManager.obtenerNombreUsuario();
        String mensaje = storageManager.obtenerMensajeMotivacional();
        int horas = storageManager.obtenerHorasNotificacionMotivacional();
        String rutaImagen = storageManager.obtenerRutaImagenPerfil();

        if (!nombre.isEmpty()) {
            binding.editNombreUsuario.setText(nombre);
        }
        
        binding.editMensajeMotivacional.setText(mensaje);
        binding.editHorasNotificacion.setText(String.valueOf(horas));

        if (rutaImagen != null) {
            File imgFile = new File(rutaImagen);
            if (imgFile.exists()) {
                binding.imagenPerfilConfig.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            }
        }
    }

    private void guardarConfiguraciones() {
        String nombre = binding.editNombreUsuario.getText().toString().trim();
        String mensaje = binding.editMensajeMotivacional.getText().toString().trim();
        String horasStr = binding.editHorasNotificacion.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese su nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mensaje.isEmpty()) {
            Toast.makeText(this, "Ingrese un mensaje motivacional", Toast.LENGTH_SHORT).show();
            return;
        }

        if (horasStr.isEmpty()) {
            Toast.makeText(this, "Ingrese las horas para notificaciones", Toast.LENGTH_SHORT).show();
            return;
        }

        int horas = Integer.parseInt(horasStr);

        if (horas < 1) {
            Toast.makeText(this, "Las horas deben ser al menos 1", Toast.LENGTH_SHORT).show();
            return;
        }

        storageManager.guardarNombreUsuario(nombre);
        storageManager.guardarMensajeMotivacional(mensaje);
        storageManager.guardarHorasNotificacionMotivacional(horas);

        if (imagenSeleccionadaUri != null) {
            storageManager.guardarImagenPerfil(imagenSeleccionadaUri);
        }

        Toast.makeText(this, "Configuraciones guardadas", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
