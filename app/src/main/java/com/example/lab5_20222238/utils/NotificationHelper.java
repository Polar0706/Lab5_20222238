package com.example.lab5_20222238.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.lab5_20222238.MainActivity;
import com.example.lab5_20222238.R;

public class NotificationHelper {

    public static final String CHANNEL_TEORICOS = "canal_teoricos";
    public static final String CHANNEL_LABORATORIOS = "canal_laboratorios";
    public static final String CHANNEL_ELECTIVOS = "canal_electivos";
    public static final String CHANNEL_OTROS = "canal_otros";
    public static final String CHANNEL_MOTIVACIONAL = "canal_motivacional";

    // IA utilizada: claude sonnet 2 /Prompt: Como crear canales de notificación en Android con diferentes prioridades y patrones de vibración para cada categoría /Comentario: La IA me explicó que desde Android O es obligatorio usar canales de notificación y cómo configurar diferentes importancias y patrones de vibración para cada tipo de curso
    public static void crearCanalesNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            NotificationChannel channelTeoricos = new NotificationChannel(
                    CHANNEL_TEORICOS,
                    "Cursos Teóricos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelTeoricos.setDescription("Notificaciones para cursos teóricos");
            channelTeoricos.enableVibration(true);
            channelTeoricos.setVibrationPattern(new long[]{0, 500, 200, 500});

            NotificationChannel channelLaboratorios = new NotificationChannel(
                    CHANNEL_LABORATORIOS,
                    "Laboratorios",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelLaboratorios.setDescription("Notificaciones para laboratorios");
            channelLaboratorios.enableVibration(true);
            channelLaboratorios.setVibrationPattern(new long[]{0, 1000, 500, 1000});

            NotificationChannel channelElectivos = new NotificationChannel(
                    CHANNEL_ELECTIVOS,
                    "Cursos Electivos",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channelElectivos.setDescription("Notificaciones para cursos electivos");
            channelElectivos.enableVibration(true);

            NotificationChannel channelOtros = new NotificationChannel(
                    CHANNEL_OTROS,
                    "Otros Cursos",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channelOtros.setDescription("Notificaciones para otros cursos");

            NotificationChannel channelMotivacional = new NotificationChannel(
                    CHANNEL_MOTIVACIONAL,
                    "Mensajes Motivacionales",
                    NotificationManager.IMPORTANCE_LOW
            );
            channelMotivacional.setDescription("Mensajes motivacionales periódicos");

            manager.createNotificationChannel(channelTeoricos);
            manager.createNotificationChannel(channelLaboratorios);
            manager.createNotificationChannel(channelElectivos);
            manager.createNotificationChannel(channelOtros);
            manager.createNotificationChannel(channelMotivacional);
        }
    }

    public static String obtenerCanalPorCategoria(String categoria) {
        if (categoria == null) return CHANNEL_OTROS;
        
        switch (categoria.toLowerCase()) {
            case "teorico":
            case "teórico":
                return CHANNEL_TEORICOS;
            case "laboratorio":
                return CHANNEL_LABORATORIOS;
            case "electivo":
                return CHANNEL_ELECTIVOS;
            default:
                return CHANNEL_OTROS;
        }
    }

    public static void mostrarNotificacionCurso(Context context, String titulo, String mensaje, String categoria) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = obtenerCanalPorCategoria(categoria);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        int iconId = obtenerIconoPorCategoria(categoria);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconId)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void mostrarNotificacionMotivacional(Context context, String mensaje) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_MOTIVACIONAL)
                .setSmallIcon(R.drawable.ic_motivacional)
                .setContentTitle("Mensaje Motivacional")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify(99999, builder.build());
    }

    private static int obtenerIconoPorCategoria(String categoria) {
        if (categoria == null) return R.drawable.ic_otros;
        
        switch (categoria.toLowerCase()) {
            case "teorico":
            case "teórico":
                return R.drawable.ic_teorico;
            case "laboratorio":
                return R.drawable.ic_laboratorio;
            case "electivo":
                return R.drawable.ic_electivo;
            default:
                return R.drawable.ic_otros;
        }
    }
}
