package com.cainzos.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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
            if (!emailAux.isEmpty() && !passwordAux.isEmpty()) {
                registerUser(emailAux, passwordAux);
            }else{
                Toast.makeText(MainActivity.this, "Usuario o Contraseña vacios, introduzca valores validos", Toast.LENGTH_SHORT).show();
            }
        } else if (view.equals(loginButton)) { //Ha sido pulsado el boton de inicioSesion

        }
    }

    //Funcion para realizar el registro del usuario en caso de que los parametros no sean nulos
    private void registerUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String id = mAuth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("email", email);
                map.put("password", password);

                mFirestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        startActivity(new Intent(MainActivity.this, GamemodeActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error al guardar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}