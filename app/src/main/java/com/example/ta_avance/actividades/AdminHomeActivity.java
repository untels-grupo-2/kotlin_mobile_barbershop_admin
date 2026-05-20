package com.example.ta_avance.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ta_avance.R;
import com.example.ta_avance.viewmodel.AdminHomeViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminHomeActivity extends AppCompatActivity {

    private AdminHomeViewModel viewModel;
    private TextView adminTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        viewModel = new ViewModelProvider(this).get(AdminHomeViewModel.class);

        adminTitle = findViewById(R.id.adminTitle);
        String nombre = getIntent().getStringExtra("nombre");
        String apellido = getIntent().getStringExtra("apellido");
        viewModel.setNombreYApellido(nombre, apellido);

        viewModel.getNombreCompleto().observe(this, texto -> adminTitle.setText(texto));

        findViewById(R.id.listarUsuario).setOnClickListener(v ->
                startActivity(new Intent(this, ListarUsuarioActivity.class)));

        findViewById(R.id.crearServicio).setOnClickListener(v ->
                startActivity(new Intent(this, GestionarServicioActivity.class)));

        findViewById(R.id.gestionHorarios).setOnClickListener(v ->
            mostrarPopupHorarios(v));

        findViewById(R.id.gestionReservas).setOnClickListener( V ->
                startActivity(new Intent(this, ReservasActivity.class)));

        findViewById(R.id.gestionBarbero).setOnClickListener(v ->
                startActivity(new Intent(this,GestionarBarberoActivity.class)));

        findViewById(R.id.listarValoracion).setOnClickListener(v ->
                startActivity(new Intent(this,ListarValoracionActivity.class)));

        findViewById(R.id.reportes).setOnClickListener(v ->
                startActivity(new Intent(this, ReporteActivity.class)));

        // Cierre de sesión
        findViewById(R.id.btnLogoutAdmin).setOnClickListener(v -> {
            viewModel.cerrarSesion();
            Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        ajustarBotonesGrid();
    }

    private void mostrarPopupHorarios(View anchorView) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_horarios, null);

        // Aplica fade-in
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        popupView.startAnimation(fadeIn);

        // Fondo opaco
        View dimBehind = new View(this);
        dimBehind.setBackgroundColor(0x88000000); // semi-transparente oscuro
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        rootView.addView(dimBehind, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        int popupWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.85); // 85% de la pantalla
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                popupWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );


        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        popupWindow.setOnDismissListener(() -> rootView.removeView(dimBehind));

        Button btnVerHorario = popupView.findViewById(R.id.btnVerHorario);
        Button btnPrepararHorario = popupView.findViewById(R.id.btnPrepararHorario);

        btnVerHorario.setOnClickListener(v -> {
            popupWindow.dismiss();
            Toast.makeText(this, "Ver horario actual", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HorarioActualActivity.class));
        });

        btnPrepararHorario.setOnClickListener(v -> {
            popupWindow.dismiss();
            Toast.makeText(this, "Preparar horario", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HorarioPrepararActivity.class));
        });
    }

    private void ajustarBotonesGrid() {
        GridLayout gridLayout = findViewById(R.id.btnGrid);

        int totalBotones = gridLayout.getChildCount();

        // Asegura que todos los botones llenen la columna
        for (int i = 0; i < totalBotones; i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof com.google.android.material.button.MaterialButton) {
                GridLayout.LayoutParams params = (GridLayout.LayoutParams) child.getLayoutParams();
                params.width = 0;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f); // 1 columna por defecto
                params.setGravity(Gravity.FILL_HORIZONTAL);
                child.setLayoutParams(params);
            }
        }

        // Si la cantidad es impar, el último botón ocupa 2 columnas
        if (totalBotones % 2 != 0) {
            View ultimo = gridLayout.getChildAt(totalBotones - 1);
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) ultimo.getLayoutParams();
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2); // Ocupa 2 columnas
            params.setGravity(Gravity.FILL_HORIZONTAL);
            ultimo.setLayoutParams(params);
        }
    }



}
