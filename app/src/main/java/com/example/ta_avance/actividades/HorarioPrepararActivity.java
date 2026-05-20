package com.example.ta_avance.actividades;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ta_avance.R;
import com.example.ta_avance.dto.barbero.BarberoDto;
import com.example.ta_avance.viewmodel.HorarioPrepararViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HorarioPrepararActivity extends AppCompatActivity {

    private HorarioPrepararViewModel viewModel;
    private LinearLayout containerDias;
    private static final Map<String, String> turnoIds = new HashMap<>();
    static {
        turnoIds.put("MAÑANA", "1");
        turnoIds.put("TARDE", "2");
        turnoIds.put("NOCHE", "3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario_preparar);

        containerDias = findViewById(R.id.containerDias);
        viewModel = new ViewModelProvider(this).get(HorarioPrepararViewModel.class);

        viewModel.getDias().observe(this, dias -> {
            containerDias.removeAllViews();
            for (String dia : dias) {
                MaterialButton diaButton = new MaterialButton(this);
                diaButton.setText(dia);
                diaButton.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_calendar_day));
                diaButton.setIconTintResource(android.R.color.white);
                diaButton.setIconPadding(16);
                diaButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                diaButton.setAllCaps(false);
                diaButton.setTypeface(ResourcesCompat.getFont(this, R.font.oswald_bold));
                diaButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.barber_black_deep));
                diaButton.setCornerRadius(24);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 16, 0, 0);
                diaButton.setLayoutParams(params);
                diaButton.setOnClickListener(v -> mostrarPopupDia(dia));
                containerDias.addView(diaButton);
            }
        });

        viewModel.getOperacionExitosa().observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );

        viewModel.getError().observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );

        Button btnConfirmar = findViewById(R.id.btnConfirmarHorario);
        btnConfirmar.setOnClickListener(v -> viewModel.confirmarHorario());

        Button btnExportarHorario = findViewById(R.id.btnExportarHorario);
        btnExportarHorario.setOnClickListener(v -> viewModel.exportarHorario(this));

        viewModel.cargarBarberos();
    }

    private void mostrarPopupDia(String dia) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_preparar_dia, null);

        TextView tituloDia = popupView.findViewById(R.id.tituloDia);
        LinearLayout contenedorTurnos = popupView.findViewById(R.id.contenedorTurnos);
        Button btnGuardar = popupView.findViewById(R.id.btnGuardar);

        tituloDia.setText("Preparación " + dia);
        contenedorTurnos.removeAllViews();

        List<String> turnos = viewModel.getTurnos().getValue();
        List<BarberoDto> barberos = viewModel.getBarberos().getValue();

        if (turnos == null || barberos == null) {
            Toast.makeText(this, "Error cargando turnos o barberos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, LinearLayout> layoutsPorTurno = new HashMap<>();

        for (String turno : turnos) {
            TextView tvTurno = new TextView(this);
            tvTurno.setText(turno);
            tvTurno.setTextSize(18);
            tvTurno.setTypeface(null, Typeface.BOLD);
            tvTurno.setPadding(0, 20, 0, 10);
            contenedorTurnos.addView(tvTurno);

            LinearLayout layoutBarberos = new LinearLayout(this);
            layoutBarberos.setOrientation(LinearLayout.VERTICAL);

            for (BarberoDto barbero : barberos) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(barbero.getNombre());
                checkBox.setTag(barbero.getBarbero_id());
                layoutBarberos.addView(checkBox);
            }
            contenedorTurnos.addView(layoutBarberos);
            layoutsPorTurno.put(turno, layoutBarberos);
        }

        PopupWindow popupWindow = new PopupWindow(popupView,
                (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        View fondoOscuro = new View(this);
        fondoOscuro.setBackgroundColor(0x88000000);
        rootView.addView(fondoOscuro, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        popupWindow.setOnDismissListener(() -> rootView.removeView(fondoOscuro));
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        btnGuardar.setOnClickListener(v -> {
            Map<Long, List<Long>> turnosPorTipo = new HashMap<>();

            for (String turno : turnos) {
                LinearLayout layout = layoutsPorTurno.get(turno);
                if (layout == null) continue;

                List<Long> barberoIdsSeleccionados = layout.getTouchables().stream()
                        .filter(view -> view instanceof CheckBox)
                        .map(view -> (CheckBox) view)
                        .filter(CheckBox::isChecked)
                        .map(cb -> ((Integer) cb.getTag()).longValue())
                        .collect(Collectors.toList());

                String turnoIdStr = turnoIds.get(turno.toUpperCase());
                if (turnoIdStr != null) {
                    Long turnoId = Long.parseLong(turnoIdStr);
                    turnosPorTipo.put(turnoId, barberoIdsSeleccionados);
                }
            }

            viewModel.guardarTurnosDia(dia, turnosPorTipo);
            popupWindow.dismiss();
        });
    }
}