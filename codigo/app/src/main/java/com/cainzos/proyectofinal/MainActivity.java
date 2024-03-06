package com.cainzos.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

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

        /*---Asignacion de ids---*/
        //Botones
        registerButton = findViewById(R.id.registro);
        loginButton = findViewById(R.id.inicio_sesion);
        googleButton = findViewById(R.id.google);
        facebookButton = findViewById(R.id.facebook);
        //Textos
        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);

        registerButton.setOnClickListener(view -> {
            String emailAux = email.getText().toString().trim();
            String passwordAux = password.getText().toString().trim();

            if (emailAux.isEmpty() && passwordAux.isEmpty()) {
                Toast.makeText(MainActivity.this, "Usuario o Contraseña vacios, introduzca valores validos", Toast.LENGTH_SHORT).show();
            }else{
                registerUser(emailAux, passwordAux);
            }
        });
    }

    //Funcion para realizar el registro del usuario en caso de que los parametros no sean nulos
    private void registerUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("_TAG", "Tarea realiazda con exito");
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String id = user.getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", id);
                    map.put("email", email);
                    map.put("password", password);

                    mFirestore.collection("user").document(id).set(map).addOnSuccessListener(unused -> {
                        finish();
                        startActivity(new Intent(MainActivity.this, GamemodeActivity.class));
                    }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error al guardar el usuario en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(MainActivity.this, "El usuario actual es nulo", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Error al crear el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}