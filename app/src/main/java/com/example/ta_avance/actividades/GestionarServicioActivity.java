package com.example.ta_avance.actividades;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ta_avance.R;
import com.example.ta_avance.adapters.ServicioAdapter;
import com.example.ta_avance.dto.servicio.ServicioDto;
import com.example.ta_avance.dto.servicio.ServicioRequest;
import com.example.ta_avance.viewmodel.GestionarServicioViewModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GestionarServicioActivity extends AppCompatActivity {

    private GestionarServicioViewModel viewModel;
    private RecyclerView recyclerView;
    private Button btnAgregarServicio;
    private List<ServicioDto> listaServicios;
    private ServicioAdapter adapter;
    private static final int REQUEST_SELECT_IMAGE = 1001;
    private Uri imagenSeleccionadaUri;

    private final Map<String, Integer> tipoServicioMap = new LinkedHashMap<String, Integer>() {{
        put("CORTES", 1);
        put("SKINCARE", 2);
        put("AFEITADO DE BARBA", 3);
        put("COLORACIÓN", 4);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_servicio);

        recyclerView = findViewById(R.id.recyclerViewServicios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnAgregarServicio = findViewById(R.id.btnAgregarServicio);
        btnAgregarServicio.setOnClickListener(v -> mostrarPopupNuevoServicio(v));

        viewModel = new ViewModelProvider(this).get(GestionarServicioViewModel.class);

        viewModel.getServicios().observe(this, servicios -> {
            listaServicios = servicios;
            adapter = new ServicioAdapter(servicios, new ServicioAdapter.OnServicioClickListener() {
                @Override
                public void onActualizar(ServicioDto servicio) {
                    mostrarPopupActualizarServicio(servicio);
                }
                @Override
                public void onEliminar(ServicioDto servicio) {
                    eliminarServicio(servicio.getServicio_id());
                }
            });
            recyclerView.setAdapter(adapter);
        });

        viewModel.getOperacionExitosa().observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );

        viewModel.getError().observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );

        cargarLista();
    }

    private void cargarLista() {
        viewModel.obtenerServicios();
    }

    private void mostrarPopupNuevoServicio(View anchorView) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_nuevo_servicio, null);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        popupView.startAnimation(fadeIn);

        View dimBehind = new View(this);
        dimBehind.setBackgroundColor(0x88000000);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        rootView.addView(dimBehind, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(() -> rootView.removeView(dimBehind));

        EditText etNombre = popupView.findViewById(R.id.etNombreServicio);
        EditText etPrecio = popupView.findViewById(R.id.etPrecioServicio);
        EditText etDescripcion = popupView.findViewById(R.id.etDescripcionServicio);
        Spinner spinnerTipo = popupView.findViewById(R.id.spinnerTipoServicio);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipoServicioMap.keySet().toArray(new String[0])
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(spinnerAdapter);

        Button btnCrear = popupView.findViewById(R.id.btnCrearServicio);
        Button btnCancelar = popupView.findViewById(R.id.btnCancelarServicio);
        Button btnSeleccionarImagenServicio = popupView.findViewById(R.id.btnSeleccionarImagenServicio);

        btnSeleccionarImagenServicio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        });

        btnCrear.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String precioStr = etPrecio.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String tipoSeleccionado = (String) spinnerTipo.getSelectedItem();

            if (!nombre.isEmpty() && !precioStr.isEmpty() && !descripcion.isEmpty() && tipoSeleccionado != null) {
                double precio = Double.parseDouble(precioStr);
                int tipoId = tipoServicioMap.get(tipoSeleccionado);
                crearNuevoServicio(nombre, precio, descripcion, tipoId);
                popupWindow.dismiss();
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> popupWindow.dismiss());
    }

    private void mostrarPopupActualizarServicio(ServicioDto servicio) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_nuevo_servicio, null);

        EditText etNombre = popupView.findViewById(R.id.etNombreServicio);
        EditText etPrecio = popupView.findViewById(R.id.etPrecioServicio);
        EditText etDescripcion = popupView.findViewById(R.id.etDescripcionServicio);
        Spinner spinnerTipo = popupView.findViewById(R.id.spinnerTipoServicio);

        etNombre.setText(servicio.getNombre());
        etPrecio.setText(String.valueOf(servicio.getPrecio()));
        etDescripcion.setText(servicio.getDescripcion());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipoServicioMap.keySet().toArray(new String[0])
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(spinnerAdapter);

        for (int i = 0; i < tipoServicioMap.size(); i++) {
            if (tipoServicioMap.values().toArray()[i].equals(servicio.getTipoServicio_id())) {
                spinnerTipo.setSelection(i);
                break;
            }
        }

        View dimBehind = new View(this);
        dimBehind.setBackgroundColor(0x88000000);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        rootView.addView(dimBehind, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        popupView.startAnimation(fadeIn);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(() -> rootView.removeView(dimBehind));

        Button btnCrear = popupView.findViewById(R.id.btnCrearServicio);
        Button btnCancelar = popupView.findViewById(R.id.btnCancelarServicio);
        Button btnSeleccionarImagenServicio = popupView.findViewById(R.id.btnSeleccionarImagenServicio);

        btnSeleccionarImagenServicio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        });

        btnCrear.setText("Actualizar");

        btnCrear.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String precioStr = etPrecio.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String tipoSeleccionado = (String) spinnerTipo.getSelectedItem();

            if (!nombre.isEmpty() && !precioStr.isEmpty() && !descripcion.isEmpty()) {
                double precio = Double.parseDouble(precioStr);
                int tipoId = tipoServicioMap.get(tipoSeleccionado);
                ServicioRequest request = new ServicioRequest(nombre, precio, descripcion, tipoId);
                viewModel.actualizarServicio(this, servicio.getServicio_id(), request, imagenSeleccionadaUri);
                popupWindow.dismiss();
                cargarLista();
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> popupWindow.dismiss());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            imagenSeleccionadaUri = data.getData();
            ImageView ivImagen = findViewById(R.id.ivFotoBarbero);
            if (ivImagen != null) {
                ivImagen.setImageURI(imagenSeleccionadaUri);
                ivImagen.setVisibility(View.VISIBLE);
            }
        }
    }

    private void crearNuevoServicio(String nombre, double precio, String descripcion, int tipoServicioId) {
        ServicioRequest request = new ServicioRequest(nombre, precio, descripcion, tipoServicioId);
        viewModel.crearServicio(this, request, imagenSeleccionadaUri);
        imagenSeleccionadaUri = null;
    }

    private void eliminarServicio(int id) {
        viewModel.eliminarServicio(id);
        cargarLista();
    }
}