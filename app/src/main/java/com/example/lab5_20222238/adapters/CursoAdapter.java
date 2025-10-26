package com.example.lab5_20222238.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_20222238.databinding.ItemCursoBinding;
import com.example.lab5_20222238.models.Curso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.CursoViewHolder> {

    private List<Curso> cursos;
    private OnCursoActionListener listener;

    public interface OnCursoActionListener {
        void onEliminarCurso(Curso curso, int position);
    }

    public CursoAdapter(OnCursoActionListener listener) {
        this.cursos = new ArrayList<>();
        this.listener = listener;
    }

    public void setCursos(List<Curso> cursos) {
        this.cursos = cursos;
        notifyDataSetChanged();
    }

    public void eliminarCurso(int position) {
        cursos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cursos.size());
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCursoBinding binding = ItemCursoBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CursoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = cursos.get(position);
        holder.bind(curso, position);
    }

    @Override
    public int getItemCount() {
        return cursos.size();
    }

    // IA utilizada: claude sonnet 2 /Prompt: Como implementar un RecyclerView adapter con ViewBinding y listener para manejo de clicks en items /Comentario: La IA me mostró cómo usar el patrón ViewHolder correctamente con ViewBinding, cómo pasar datos al bind y configurar listeners en cada item para acciones como eliminar
    class CursoViewHolder extends RecyclerView.ViewHolder {
        private ItemCursoBinding binding;

        public CursoViewHolder(ItemCursoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Curso curso, int position) {
            binding.textNombreCurso.setText(curso.getNombre());
            binding.textCategoria.setText("Categoría: " + curso.getCategoria());
            
            String frecuenciaText = "Cada " + curso.getFrecuenciaDias() + 
                    (curso.getFrecuenciaDias() == 1 ? " día" : " días");
            binding.textFrecuencia.setText(frecuenciaText);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String fechaHora = sdf.format(new Date(curso.getProximaSesionTimestamp()));
            binding.textProximaSesion.setText(fechaHora);

            int iconId = obtenerIconoPorCategoria(curso.getCategoria());
            binding.iconCategoria.setImageResource(iconId);

            binding.btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEliminarCurso(curso, position);
                }
            });
        }

        private int obtenerIconoPorCategoria(String categoria) {
            if (categoria == null) return com.example.lab5_20222238.R.drawable.ic_otros;
            
            switch (categoria.toLowerCase()) {
                case "teorico":
                case "teórico":
                    return com.example.lab5_20222238.R.drawable.ic_teorico;
                case "laboratorio":
                    return com.example.lab5_20222238.R.drawable.ic_laboratorio;
                case "electivo":
                    return com.example.lab5_20222238.R.drawable.ic_electivo;
                default:
                    return com.example.lab5_20222238.R.drawable.ic_otros;
            }
        }
    }
}
