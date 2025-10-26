package com.example.lab5_20222238.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.lab5_20222238.R;
import com.example.lab5_20222238.databinding.ActivityCrearCursoBinding;
import com.example.lab5_20222238.models.Curso;
import com.example.lab5_20222238.utils.StorageManager;
import com.example.lab5_20222238.workers.CursoNotificationWorker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CrearCursoActivity extends AppCompatActivity {

    private ActivityCrearCursoBinding binding;
    private StorageManager storageManager;
    private Calendar calendarioSeleccionado;
    private boolean fechaHoraSeleccionada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCrearCursoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageManager = new StorageManager(this);
        calendarioSeleccionado = Calendar.getInstance();

        configurarSpinnerCategoria();

        binding.btnSeleccionarFecha.setOnClickListener(v -> mostrarDatePicker());
        binding.btnSeleccionarHora.setOnClickListener(v -> mostrarTimePicker());
        binding.btnGuardarCurso.setOnClickListener(v -> guardarCurso());
    }

    private void configurarSpinnerCategoria() {
        String[] categorias = {"Teórico", "Laboratorio", "Electivo", "Otros"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categorias
        );
        binding.spinnerCategoria.setAdapter(adapter);
    }

    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendarioSeleccionado.set(Calendar.YEAR, year);
                    calendarioSeleccionado.set(Calendar.MONTH, month);
                    calendarioSeleccionado.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    actualizarTextoFechaHora();
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void mostrarTimePicker() {
        Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendarioSeleccionado.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendarioSeleccionado.set(Calendar.MINUTE, minute);
                    calendarioSeleccionado.set(Calendar.SECOND, 0);
                    fechaHoraSeleccionada = true;
                    actualizarTextoFechaHora();
                },
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void actualizarTextoFechaHora() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        binding.textFechaHoraSeleccionada.setText(sdf.format(calendarioSeleccionado.getTime()));
    }

    private void guardarCurso() {
        String nombre = binding.editNombre.getText().toString().trim();
        String categoria = binding.spinnerCategoria.getText().toString().trim();
        String frecuenciaStr = binding.editFrecuencia.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre del curso", Toast.LENGTH_SHORT).show();
            return;
        }

        if (categoria.isEmpty()) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        if (frecuenciaStr.isEmpty()) {
            Toast.makeText(this, "Ingrese la frecuencia de estudio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fechaHoraSeleccionada) {
            Toast.makeText(this, "Seleccione fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        int frecuencia = Integer.parseInt(frecuenciaStr);
        String cursoId = UUID.randomUUID().toString();

        Curso nuevoCurso = new Curso(
                cursoId,
                nombre,
                categoria,
                frecuencia,
                calendarioSeleccionado.getTimeInMillis()
        );

        storageManager.agregarCurso(nuevoCurso);

        programarNotificacionCurso(nuevoCurso);

        Toast.makeText(this, "Curso guardado exitosamente", Toast.LENGTH_SHORT).show();
        finish();
    }

    // IA utilizada: claude sonnet 2 /Prompt: Como programar notificaciones en el futuro usando WorkManager calculando el delay en milisegundos /Comentario: La IA me ayudó a entender cómo calcular el tiempo de espera restando el timestamp actual del timestamp futuro, y cómo usar ese delay con WorkManager para programar la notificación en el momento exacto
    private void programarNotificacionCurso(Curso curso) {
        long delay = curso.getProximaSesionTimestamp() - System.currentTimeMillis();

        if (delay > 0) {
            Data inputData = new Data.Builder()
                    .putString("curso_id", curso.getId())
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(CursoNotificationWorker.class)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(this).enqueue(workRequest);
        }
    }
}
