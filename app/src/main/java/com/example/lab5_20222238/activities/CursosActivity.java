package com.example.lab5_20222238.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab5_20222238.adapters.CursoAdapter;
import com.example.lab5_20222238.databinding.ActivityCursosBinding;
import com.example.lab5_20222238.models.Curso;
import com.example.lab5_20222238.utils.StorageManager;

import java.util.List;

public class CursosActivity extends AppCompatActivity implements CursoAdapter.OnCursoActionListener {

    private ActivityCursosBinding binding;
    private StorageManager storageManager;
    private CursoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCursosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageManager = new StorageManager(this);

        configurarRecyclerView();

        binding.btnAgregarCurso.setOnClickListener(v -> {
            Intent intent = new Intent(CursosActivity.this, CrearCursoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCursos();
    }

    private void configurarRecyclerView() {
        adapter = new CursoAdapter(this);
        binding.recyclerViewCursos.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCursos.setAdapter(adapter);
    }

    private void cargarCursos() {
        List<Curso> cursos = storageManager.obtenerCursos();
        
        if (cursos.isEmpty()) {
            binding.textMensajeVacio.setVisibility(View.VISIBLE);
            binding.recyclerViewCursos.setVisibility(View.GONE);
        } else {
            binding.textMensajeVacio.setVisibility(View.GONE);
            binding.recyclerViewCursos.setVisibility(View.VISIBLE);
            adapter.setCursos(cursos);
        }
    }

    @Override
    public void onEliminarCurso(Curso curso, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Curso")
                .setMessage("¿Estás seguro de que deseas eliminar el curso \"" + curso.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    storageManager.eliminarCurso(curso.getId());
                    adapter.eliminarCurso(position);
                    cargarCursos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
