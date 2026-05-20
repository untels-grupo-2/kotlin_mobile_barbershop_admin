package com.example.ta_avance.actividades;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ta_avance.R;
import com.example.ta_avance.viewmodel.HorarioActualViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HorarioActualActivity extends AppCompatActivity {
    private HorarioActualViewModel viewModel;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario_actual);

        container = findViewById(R.id.containerDiasHorarioActual);
        viewModel = new ViewModelProvider(this).get(HorarioActualViewModel.class);

        // 🔁 Observar LiveData
        viewModel.getHorarios().observe(this, semana -> {
            container.removeAllViews();

            String[] ordenDias = {"LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO"};
            String[] ordenTurnos = {"MAÑANA", "TARDE", "NOCHE"};

            for (String dia : ordenDias) {
                if (semana.containsKey(dia)) {
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

                    LinearLayout turnosLayout = new LinearLayout(this);
                    turnosLayout.setOrientation(LinearLayout.VERTICAL);
                    turnosLayout.setVisibility(View.GONE);
                    turnosLayout.setPadding(32, 12, 12, 12);
                    turnosLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_turno_card));
                    turnosLayout.setElevation(4f);

                    Map<String, List<String>> turnos = semana.get(dia);
                    for (String turno : ordenTurnos) {
                        if (turnos.containsKey(turno)) {
                            List<String> barberos = turnos.get(turno);
                            String barberosTexto = String.join(", ", barberos);

                            TextView turnoView = new TextView(this);
                            turnoView.setText(turno + ": " + barberosTexto);
                            turnoView.setTextSize(15);
                            turnoView.setTextColor(ContextCompat.getColor(this, R.color.barber_black_deep));
                            turnoView.setTypeface(ResourcesCompat.getFont(this, R.font.oswald_regular));
                            turnoView.setPadding(12, 8, 12, 8);

                            turnosLayout.addView(turnoView);
                        }
                    }

                    diaButton.setOnClickListener(v -> {
                        turnosLayout.setVisibility(
                                turnosLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
                        );
                    });

                    container.addView(diaButton);
                    container.addView(turnosLayout);
                }
            }

        });

        Button btnExportarHorario = findViewById(R.id.btnExportarHorarioSemanal);
        btnExportarHorario.setOnClickListener(v -> {
            viewModel.exportarHorario(this);
        });

        // 🚀 Llamada real a la API
        viewModel.cargarHorarios();
    }
}
