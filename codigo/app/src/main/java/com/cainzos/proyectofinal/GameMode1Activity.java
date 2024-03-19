package com.cainzos.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GameMode1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode1);
        showDialog();
    }

    void showDialog() {
        final String[] opciones = {"EZ PZ", "NORMAL", "TRYHARD"};

        // Inflar el diseño XML del diálogo
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.players_dialog);
        dialog.setCanceledOnTouchOutside(false);

        final TextView optionSelected = dialog.findViewById(R.id.dificultySelected);
        final EditText input = dialog.findViewById(R.id.input); // Encontrar el EditText dentro del diálogo
        Button acceptButton = dialog.findViewById(R.id.acceptButton);
        Button backButton = dialog.findViewById(R.id.backButton);

        dialog.show();

        backButton.setOnClickListener(v -> finish());

        optionSelected.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(GameMode1Activity.this);
            builder.setTitle("Selecciona la DIFICULTAD");
            builder.setItems(opciones, (dialogInterface, i) -> optionSelected.setText(opciones[i]));
            builder.show();
        });

        acceptButton.setOnClickListener(v -> {
            String numeroIngresado = input.getText().toString();
            try {
                int numero = Integer.parseInt(numeroIngresado);
                // Aquí puedes hacer algo con el número entero
                dialog.dismiss(); // Cerrar el diálogo
            } catch (NumberFormatException e) {
                // El valor ingresado no es un entero
                AlertDialog.Builder builder = new AlertDialog.Builder(GameMode1Activity.this);
                builder.setTitle("Error");
                builder.setMessage("Por favor, ingrese un número entero válido.");
                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    // No hacer nada o manejar la situación como prefieras
                });
                builder.show();
            }
        });
    }
}