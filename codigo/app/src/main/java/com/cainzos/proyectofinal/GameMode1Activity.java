package com.cainzos.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

public class GameMode1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode1);
        showDialog();
    }

    void showDialog(){
        final String[] opciones = {"EZ PZ", "NORMAL", "TRYHARD"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona la DIFICULTAD");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String opcionSeleccionada = opciones[which];
                //manejar aqui la seleccion
                showNumberInputDialog();
            }
        });
        builder.show();
    }

    void showNumberInputDialog(){
        final EditText input = new EditText(this);
        input.setHint("Ingresar numero de jugadores");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("NÚMERO DE JUGADORES:");
        builder.setView(input);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String numeroIngresado = input.getText().toString();
                //manejar aqui numero ingresado
            }
        });
        builder.setNegativeButton("Atrás", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                showDialog();
            }
        });
        builder.show();
    }
}