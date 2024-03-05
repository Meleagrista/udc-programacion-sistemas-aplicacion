package com.cainzos.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /*Variables varias*/
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    /*Inicializamos los elementos del layout*/
    Button registerButton, loginButton;
    ImageButton googleButton, facebookButton;
    EditText password, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Creacion de elementos de la interfaz*/
        setUp();

    }

    public void setUp() {
        /*---Asignacion de ids---*/
        //Botones
        registerButton = findViewById(R.id.registro);
        loginButton = findViewById(R.id.inicio_sesion);
        googleButton = findViewById(R.id.google);
        facebookButton = findViewById(R.id.facebook);
        //Textos
        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);

        /*---SetOnClickListener---*/
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //Comprobamos que boton ha sido pulsado y actuamos en consecuencia
        if (view.equals(registerButton)) { //Ha sido pulsado el boton de registrarse
            String emailAux = email.getText().toString().trim();
            String passwordAux = password.getText().toString().trim();
            if (!emailAux.isEmpty() || !passwordAux.isEmpty()) {
                registerUser(emailAux, passwordAux);
            }else{
                Toast.makeText(this, "Usuario o Contraseña vacios, introduzca valores validos", Toast.LENGTH_SHORT).show();
            }
        } else if (view.equals(loginButton)) { //Ha sido pulsado el boton de inicioSesion

        }
    }

    //Funcion para realizar el registro del usuario en caso de que los parametros no sean nulos
    private void registerUser(String email, String password){

    }

}