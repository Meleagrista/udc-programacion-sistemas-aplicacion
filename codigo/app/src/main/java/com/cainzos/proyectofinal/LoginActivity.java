package com.cainzos.proyectofinal;

import androidx.activity.result.ActivityResultLauncher;
import com.cainzos.proyectofinal.databinding.ActivityLoginBinding;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cainzos.proyectofinal.recursos.UserDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class LoginActivity extends AppCompatActivity{

    /*Variables firebase*/
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    /*Inicializamos los elementos del layout*/
    Button registerButton, loginButton, anonymousButton;
    ImageButton googleButton, facebookButton;
    EditText password, email;

    /*Variables intents*/
    private ActivityResultLauncher<Intent> myStartActivityForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Bindings*/
        com.cainzos.proyectofinal.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
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

            if (emailAux.isEmpty() || passwordAux.isEmpty()) { //Comprobamos que el correo o la contraseña sean nulas
                Toast.makeText(LoginActivity.this, R.string.error_elemento_vacio_iniciosesion, Toast.LENGTH_SHORT).show();
            }else{
                registerUser(emailAux, passwordAux);
            }
        });

        /*---Logica cuando se pulsa el boton de inicio de sesion---*/
        loginButton.setOnClickListener(view -> {
            String emailAux = email.getText().toString().trim();
            String passwordAux = password.getText().toString().trim();

            if(emailAux.isEmpty() || passwordAux.isEmpty() ){ //Comprobamos que el correo o la contraseña sean nulas
                Toast.makeText(LoginActivity.this, R.string.error_elemento_vacio_registro, Toast.LENGTH_SHORT).show();
            }else{
                loginUser(emailAux, passwordAux);
            }
        });

        /*---Logica cuando se pulsa el boton de entrar como anonimo---*/
        anonymousButton.setOnClickListener(view -> loginAnonymous());

        /*---Comprobar sesión al abrir la aplicación---*/
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            redirectToGamemodeActivity();
        }
    }

    /*---Funcion que se encarga de dirigir directamente a la siguiente Actividad en caso de que ya hubiese una sesion guardada---*/
    private void redirectToGamemodeActivity() {
        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
        finish(); // Termina la actividad actual para evitar que el usuario regrese a ella usando el botón de retroceso
    }

    /*---Funcion para loguear a un usuario---*/
    private void loginUser(String emailAux, String passwordAux) {
        mAuth.signInWithEmailAndPassword(emailAux, passwordAux).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                saveSessionInfo(); //Una vez iniciada la sesion se registran los datos de sesion para cuando vuelva a abrirse la app
                redirectToGamemodeActivity();
            }else{
                Toast.makeText(LoginActivity.this, R.string.error_msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*---Funcion para recuperar la sesion anterior del usuario en caso de haber cerrado y vuelto a abrir la app---*/
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


    /*--- Funcion para realizar el registro del usuario en caso de que los parametros no sean nulos ---*/
    private void registerUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("_TAG", "Tarea realizada con éxito");
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    //Obtenemos el Id del usuario
                    String id = user.getUid();

                    //Creamos un nombre de usuario generico
                    String userName = "Non_user";

                    // Obtener los primeros cinco caracteres del correo electrónico
                    String emailPrefix = getEmailPrefix(email);

                    // Crear el tag con "#" y los primeros cinco caracteres del correo electrónico
                    String tag = "#" + emailPrefix;

                    //Creamos un map con todos los parametros del usuario para poder almacenarlo
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", id);
                    map.put("email", email);
                    map.put("password", password);
                    map.put("username", userName);
                    map.put("tag", tag);

                    //Registra al usuario en la base de datos y llama a la actividad de Gamemode
                    mFirestore.collection(getString(R.string.collection_path_users)).document(id).set(map).addOnSuccessListener(unused -> {
                        // Inicializar UserDataManager al registrarse
                        UserDataManager.getInstance();
                        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                        // Cierra la actividad actual después de iniciar la actividad GamemodeActivity
                        finish();
                    });
                } else {
                    Toast.makeText(LoginActivity.this, R.string.error_usr_null, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.error_creating_user) + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para obtener los primeros cinco caracteres del correo electrónico
    private String getEmailPrefix(String email) {
        String[] parts = email.split("@");
        StringBuilder prefix = new StringBuilder(parts[0]);
        if (prefix.length() < 5) {
            // Si el prefijo tiene menos de cinco caracteres, completarlo con caracteres aleatorios
            Random random = new Random();
            while (prefix.length() < 5) {
                char randomChar = (char) (random.nextInt(26) + 'a'); // Generar un carácter aleatorio
                prefix.append(randomChar);
            }
        } else {
            prefix = new StringBuilder(prefix.substring(0, 5)); // Tomar los primeros cinco caracteres
        }
        return prefix.toString();
    }


    /*---Funcion para registrar a un usuario de forma anonima---*/
    private void loginAnonymous(){
        mAuth.signInAnonymously().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               startActivity(new Intent(LoginActivity.this, MenuActivity.class));
           }
        }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, R.string.error_login_anonymous, Toast.LENGTH_SHORT).show());
    }
}