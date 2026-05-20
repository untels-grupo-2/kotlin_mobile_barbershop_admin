package com.example.ta_avance.actividades;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ta_avance.R;
import com.example.ta_avance.adapters.ReservaAdapter;
import com.example.ta_avance.dto.login.LoginRequest;
import com.example.ta_avance.dto.reserva.DtoReserva;
import com.example.ta_avance.viewmodel.ReservasViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReservasActivity extends AppCompatActivity {

    private ReservasViewModel viewModel;
    private RecyclerView recyclerView;
    private TextInputEditText etFechaSeleccionada;
    private Spinner spinnerEstado;
    private Button btnFiltrar;
    private TextView tituloReservas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas);

        Locale nuevoLocale = new Locale("es", "ES");
        Locale.setDefault(nuevoLocale);
        Configuration config = getResources().getConfiguration();
        config.setLocale(nuevoLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        recyclerView = findViewById(R.id.recyclerViewReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spinnerEstado = findViewById(R.id.spinnerEstado);
        etFechaSeleccionada = findViewById(R.id.etFechaSeleccionada);
        btnFiltrar = findViewById(R.id.btnFiltrar);
        tituloReservas = findViewById(R.id.tituloReservas);

        String[] estados = {"CREADA", "CONFIRMADA", "REALIZADA"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ReservasViewModel.class);

        viewModel.getReservas().observe(this, reservas -> {
            String estado = spinnerEstado.getSelectedItem().toString();
            ReservaAdapter adapterRv = new ReservaAdapter(reservas, new ReservaAdapter.OnReservaClickListener() {
                @Override
                public void onVerDetallesClick(DtoReserva reserva) {
                    mostrarPopupDetalle(reserva);
                }

                @Override
                public void onReservaRealizadaClick(DtoReserva reserva) {
                    viewModel.cambiarEstadoReserva(reserva.getReservaId(), "REALIZADA", "Reserva completada correctamente");
                }
            }, estado);
            recyclerView.setAdapter(adapterRv);
        });

        viewModel.getMensajeError().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getCambioEstadoExitoso().observe(this, exitoso -> {
            if (Boolean.TRUE.equals(exitoso)) {
                Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show();
                cargarReservas();
            }
        });

        spinnerEstado.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                etFechaSeleccionada.setText("");
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        etFechaSeleccionada.setOnClickListener(v -> mostrarDatePicker());
        btnFiltrar.setOnClickListener(v -> cargarReservas());
    }

    private void mostrarDatePicker() {
        String estadoSeleccionado = spinnerEstado.getSelectedItem().toString();
        LocalDate hoy = LocalDate.now();
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona una fecha");

        if (estadoSeleccionado.equals("CREADA") || estadoSeleccionado.equals("CONFIRMADA")) {
            LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);
            long fechaMin = hoy.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long fechaMax = finSemana.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    .setStart(fechaMin)
                    .setEnd(fechaMax)
                    .setValidator(CompositeDateValidator.allOf(Arrays.asList(
                            DateValidatorPointForward.from(fechaMin),
                            DateValidatorPointBackward.before(fechaMax)
                    )))
                    .build();
            builder.setSelection(fechaMin).setCalendarConstraints(constraints);
        } else if (estadoSeleccionado.equals("REALIZADA")) {
            long fechaMax = hoy.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    .setEnd(fechaMax)
                    .setValidator(DateValidatorPointBackward.before(fechaMax))
                    .build();
            builder.setSelection(fechaMax).setCalendarConstraints(constraints);
        }

        MaterialDatePicker<Long> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            LocalDate fechaSeleccionada = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            etFechaSeleccionada.setText(fechaSeleccionada.toString());
        });
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void cargarReservas() {
        String fecha = etFechaSeleccionada.getText().toString();
        String estado = spinnerEstado.getSelectedItem().toString();

        if (estado.equals("CREADA") || estado.equals("CONFIRMADA")) {
            if (fecha.isEmpty()) {
                Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        tituloReservas.setText("Reservas - " + estado);
        viewModel.cargarReservas(fecha, estado);
    }

    private void mostrarPopupDetalle(DtoReserva reserva) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_reserva_detalle, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(popupView).create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextView tvUsuario = popupView.findViewById(R.id.tvDetalleUsuario);
        TextView tvBarbero = popupView.findViewById(R.id.tvDetalleBarbero);
        TextView tvHorario = popupView.findViewById(R.id.tvDetalleHorario);
        TextView tvServicio = popupView.findViewById(R.id.tvDetalleServicio);
        TextView tvFechaReserva = popupView.findViewById(R.id.tvDetalleFechaReserva);
        TextView tvFechaCreacion = popupView.findViewById(R.id.tvDetalleFechaCreacion);
        TextView tvPrecio = popupView.findViewById(R.id.tvDetallePrecio);
        TextView tvAdicionales = popupView.findViewById(R.id.tvDetalleAdicionales);
        ImageView ivComprobante = popupView.findViewById(R.id.ivComprobante);
        MaterialButton btnConfirmar = popupView.findViewById(R.id.btnConfirmarReserva);
        MaterialButton btnCancelar = popupView.findViewById(R.id.btnCancelarReserva);

        tvUsuario.setText("Usuario: " + reserva.getUsuarioNombre());
        tvBarbero.setText("Barbero: " + reserva.getBarberoNombre());
        tvHorario.setText("Horario: " + reserva.getHorarioRango());
        tvServicio.setText("Servicio: " + reserva.getServicioNombre());
        tvFechaReserva.setText("Fecha reserva: " + reserva.getFechaReserva());
        tvFechaCreacion.setText("Creado el: " + reserva.getFechaCreacion());
        tvPrecio.setText("Precio: s/" + reserva.getPrecioServicio());
        tvAdicionales.setText("Adicionales: " + reserva.getAdicionales());

        if (reserva.getUrlPago() != null && !reserva.getUrlPago().isEmpty()) {
            Glide.with(this).load(reserva.getUrlPago()).into(ivComprobante);
        } else {
            ivComprobante.setVisibility(View.GONE);
        }

        btnConfirmar.setOnClickListener(v -> {
            dialog.dismiss();
            viewModel.obtenerUsuarioPorId(reserva.getUsuarioId());
            viewModel.getUsuarioPorIdLiveData().observe(this, usuario -> {
                if (usuario != null) {
                    enviarWsp(usuario, reserva);
                    viewModel.cambiarEstadoReserva(reserva.getReservaId(), "CONFIRMADA", "Reserva confirmada por el administrador.");
                }
            });
        });

        btnCancelar.setOnClickListener(v -> {
            dialog.dismiss();
            mostrarPopupMotivo(reserva.getReservaId(), "CANCELADA");
        });

        dialog.show();
    }

    private void mostrarPopupMotivo(Long reservaId, String nuevoEstado) {
        View motivoView = LayoutInflater.from(this).inflate(R.layout.popup_motivo_cancelacion, null);
        AlertDialog motivoDialog = new AlertDialog.Builder(this).setView(motivoView).create();

        EditText etMotivoCancelacion = motivoView.findViewById(R.id.etMotivoCancelacion);
        MaterialButton btnEnviarMotivo = motivoView.findViewById(R.id.btnEnviarMotivo);

        btnEnviarMotivo.setOnClickListener(v -> {
            String motivo = etMotivoCancelacion.getText().toString().trim();
            viewModel.cambiarEstadoReserva(reservaId, nuevoEstado, motivo);
            motivoDialog.dismiss();
        });

        motivoDialog.show();
    }

    private void enviarWsp(LoginRequest usuario, DtoReserva reserva) {
        String numero = usuario.getCelular();
        String mensaje = "Hola *" + usuario.getNombre() + "*, tu reserva ha sido confirmada 🎉\n\n" +
                "📅 Fecha: *" + reserva.getFechaReserva() + "*\n" +
                "🕒 Horario: *" + reserva.getHorarioRango() + "*\n" +
                "💇 Servicio: *" + reserva.getServicioNombre() + "*\n\n" +
                "Por favor, acude puntual a tu cita. ¡Gracias por elegirnos! 💈";

        try {
            String uri = "https://wa.me/51" + numero + "?text=" + java.net.URLEncoder.encode(mensaje, "UTF-8");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(uri));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }
}