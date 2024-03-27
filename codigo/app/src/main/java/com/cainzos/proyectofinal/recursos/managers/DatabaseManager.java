package com.cainzos.proyectofinal.recursos.managers;

import android.app.Activity;
import android.widget.Toast;

import com.cainzos.proyectofinal.recursos.objects.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseManager {

    private static DatabaseManager instance; // Instancia única de DatabaseManager

    //private final FirebaseUser currentUser;
    private final FirebaseFirestore mFirestore;

    // Constructor privado para evitar la creación de instancias directas
    private DatabaseManager() {
        //currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
    }

    // Método estático para obtener la instancia única de DatabaseManager
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Interfaz para manejar la carga de usuarios
    public interface OnUsersLoadedListener {
        void onUsersLoaded(List<User> users);
    }

    // Método para cargar todos los usuarios desde Firestore
    public void getAllUsers(Activity activity, OnUsersLoadedListener listener) {
        List<User> allUsers = new ArrayList<>();

        mFirestore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener los datos del documento y crear un objeto User
                            String userId = document.getString("id");
                            String userEmail = document.getString("email");
                            String userName = document.getString("username");
                            String tag = document.getString("tag");
                            String password = document.getString("password");

                            User user = new User(userId, userEmail, userName, tag, password);

                            // Agregar el usuario a la lista
                            allUsers.add(user);
                        }
                        // Llamar al listener con la lista de usuarios cargada
                        listener.onUsersLoaded(allUsers);
                    } else {
                        Toast.makeText(activity, "Error al obtener usuarios: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
