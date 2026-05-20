package com.example.ta_avance.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ta_avance.R;
import com.example.ta_avance.viewmodel.RecuperarContraViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecuperarContraActivity extends AppCompatActivity {

    private EditText campoUsuario, campoCorreo;
    private Button btnRecuperarContra, btnVolverLogin;
    private RecuperarContraViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contra);

        campoUsuario = findViewById(R.id.campoUsuario);
        campoCorreo = findViewById(R.id.campoEmail);
        btnRecuperarContra = findViewById(R.id.btnRecuperarContra);
        btnVolverLogin = findViewById(R.id.btnVolverLogin);

        viewModel = new ViewModelProvider(this).get(RecuperarContraViewModel.class);

        btnRecuperarContra.setOnClickListener(v -> {
            String usuario = campoUsuario.getText().toString().trim();
            String correo = campoCorreo.getText().toString().trim();

            if (usuario.isEmpty() || correo.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.recuperar(usuario, correo);
        });

        btnVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RecuperarContraActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        viewModel.getResultado().observe(this, mensaje -> {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            finish();
        });

        viewModel.getError().observe(this, mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        );
    }
}