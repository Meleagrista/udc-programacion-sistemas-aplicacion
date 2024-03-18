package com.cainzos.proyectofinal;

import androidx.activity.result.ActivityResultLauncher;
import com.cainzos.proyectofinal.databinding.ActivityMainBinding;
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

    /*Variables firebase*/
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    /*Inicializamos los elementos del layout*/
    Button registerButton, loginButton, anonymousButton;
    ImageButton googleButton, facebookButton;
    EditText password, email;

    /*Variables intents*/
    private ActivityResultLauncher<Intent> myStartActivityForResult;

    /*Bindings*/
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*---Gestion de firebase---*/
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        /*---Asignacion de ids---*/
        //Botones
        registerButton = binding.registro;
        loginButton = binding.inicioSesion;
        googleButton = binding.google;
        facebookButton = binding.facebook;
        anonymousButton = binding.anonimo;
        //Textos
        email = binding.Email;
        password = binding.Password;

        /*---Logica cuando se pulsa el boton de registro de usuario---*/
        registerButton.setOnClickListener(view -> {
            String emailAux = email.getText().toString().trim();
            String passwordAux = password.getText().toString().trim();

            if (emailAux.isEmpty() || passwordAux.isEmpty()) {
                Toast.makeText(MainActivity.this, R.string.error_elemento_vacio_iniciosesion, Toast.LENGTH_SHORT).show();
            }else{
                registerUser(emailAux, passwordAux);
            }
        });

        /*---Logica cuando se pulsa el boton de inicio de sesion---*/
        loginButton.setOnClickListener(view -> {
            String emailAux = email.getText().toString().trim();
            String passwordAux = password.getText().toString().trim();

            if(emailAux.isEmpty() && passwordAux.isEmpty() ){
                Toast.makeText(MainActivity.this, R.string.error_elemento_vacio_registro, Toast.LENGTH_SHORT).show();
            }else{
                loginUser(emailAux, passwordAux);
            }
        });

        /*---Logica cuando se pulsa el boton de entrar como anonimo---*/
        anonymousButton.setOnClickListener(view -> {
            loginAnonymous();
        });

        // Comprobar sesión al abrir la aplicación
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            redirectToGamemodeActivity();
        }
    }

    private void redirectToGamemodeActivity() {
        startActivity(new Intent(MainActivity.this, GamemodeActivity.class));
        finish(); // Termina la actividad actual para evitar que el usuario regrese a ella usando el botón de retroceso
    }

    private void loginUser(String emailAux, String passwordAux) {
        mAuth.signInWithEmailAndPassword(emailAux, passwordAux).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                saveSessionInfo();
                redirectToGamemodeActivity();
            }else{
                Toast.makeText(MainActivity.this, R.string.error_msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSessionInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("userId", userId);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(getString(R.string.collection_path_session)).document(userId)
                    .set(sessionData)
                    .addOnSuccessListener(aVoid -> Log.d("_TAG", "Información de sesión guardada con éxito en Firestore"))
                    .addOnFailureListener(e -> Log.e("_TAG", "Error al guardar la información de sesión en Firestore", e));
        } else {
            Log.e("_TAG", "El usuario actual es nulo");
        }
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
                    map.put("username", "");

                    mFirestore.collection(getString(R.string.collection_path_users)).document(id).set(map).addOnSuccessListener(unused -> {
                        startActivity(new Intent(MainActivity.this, GamemodeActivity.class));
                        // Cierra la actividad actual después de iniciar la actividad GamemodeActivity
                        finish();
                    });
                } else {
                    Toast.makeText(MainActivity.this, R.string.error_usr_null, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.error_creating_user) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginAnonymous(){
        mAuth.signInAnonymously().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               startActivity(new Intent(MainActivity.this, GamemodeActivity.class));
           }
        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, R.string.error_login_anonymous, Toast.LENGTH_SHORT).show());
    }
}