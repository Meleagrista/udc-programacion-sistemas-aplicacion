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
        final EditText input = new EditText(this);
        input.setHint("Ingresar número de jugadores");

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.players_dialog);

        final TextView optionSelected = dialog.findViewById(R.id.dificultySelected);
        Button acceptButton = dialog.findViewById(R.id.acceptButton);
        Button backButton = dialog.findViewById(R.id.backButton);

        dialog.show();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        optionSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameMode1Activity.this);
                builder.setTitle("Selecciona la DIFICULTAD");
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        optionSelected.setText(opciones[i]); // Mostrar la opción seleccionada en el diálogo personalizado
                    }
                });
                builder.show();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroIngresado = input.getText().toString();
                // Manejar el número ingresado aquí
                dialog.dismiss(); // Cerrar el diálogo
            }
        });
    }

}