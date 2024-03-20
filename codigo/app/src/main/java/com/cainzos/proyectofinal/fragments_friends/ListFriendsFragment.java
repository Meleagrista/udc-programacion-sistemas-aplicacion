package com.cainzos.proyectofinal.fragments_friends;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import com.cainzos.proyectofinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListFriendsFragment extends Fragment {

    private EditText editTextName;
    private Button buttonEditName, buttonDelete;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private LinearLayout containerFriends;
    private View rootView;
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_list_friends_fragment, container, false);

        // Obtener instancia de FirebaseFirestore y FirebaseAuth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Obtener referencias a los elementos de interfaz de usuario
        editTextName = rootView.findViewById(R.id.editTextName);
        buttonEditName = rootView.findViewById(R.id.editUserName);
        containerFriends = rootView.findViewById(R.id.containerFriends);

        editTextName.setEnabled(false);

        // Obtener el nombre de usuario de Firestore y establecerlo en el EditText
        setUserNameInEditText();

        // Lógica para modificar el nombre del usuario
        buttonEditName.setOnClickListener(v -> {
            if (!editTextName.isEnabled()) {
                // Habilitar la edición del campo de nombre y cambiar el texto del botón a "Aceptar"
                editTextName.setEnabled(true);
                buttonEditName.setText("Aceptar");
            } else {
                // Obtener el nuevo nombre del campo de nombre
                String newName = editTextName.getText().toString().trim();
                // Actualizar el nombre en la base de datos
                updateUserName(newName);
                // Deshabilitar la edición del campo de nombre y restaurar el texto del botón
                editTextName.setEnabled(false);
                buttonEditName.setText("Editar");
            }
        });

        //Llamada al metodo de creacion de los views de amigos
        loadFriends();

        return rootView;
    }

    private void loadFriends() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && !mAuth.getCurrentUser().isAnonymous()) {
            String currentUserEmail = currentUser.getEmail();

            db.collection("friends")
                    .whereEqualTo("friend_1", currentUserEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String friend2 = document.getString("friend_2");

                            // Asegurarse de que el usuario actual no sea amigo consigo mismo
                            if (!friend2.equals(currentUserEmail)) {
                                addFriendToLayout(friend2);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Error al cargar los amigos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            db.collection("friends")
                    .whereEqualTo("friend_2", currentUserEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String friend1 = document.getString("friend_1");

                            // Asegurarse de que el usuario actual no sea amigo consigo mismo
                            if (!friend1.equals(currentUserEmail)) {
                                addFriendToLayout(friend1);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Error al cargar los amigos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } else if (mAuth.getCurrentUser().isAnonymous()) {
            Toast.makeText(getActivity(), "Inicia sesión para poder tener amigos", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Usuario actual nulo", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para agregar un amigo al layout
    private void addFriendToLayout(String email) {
        db.collection("users").whereEqualTo("email", email).get().addOnSuccessListener(receiverQueryDocumentSnapshots->{
            for (QueryDocumentSnapshot receiverDocumentSnapshot : receiverQueryDocumentSnapshots){
                String userName = receiverDocumentSnapshot.getString("username");
                if (userName == null || userName.isEmpty()) {
                    userName = "Anonymous123";
                }

                // Inflar el diseño del amigo
                View friendView = getLayoutInflater().inflate(R.layout.friend_item, null);

                // Obtener referencias de vistas dentro del diseño inflado
                textView = friendView.findViewById(R.id.textViewFriendName);
                buttonDelete = friendView.findViewById(R.id.buttonDelete);

                //Obtenemos el correo del usuario
                FirebaseUser currentUser = mAuth.getCurrentUser();
                assert currentUser != null;
                String currentUserEmail = currentUser.getEmail();

                buttonDelete.setOnClickListener(view -> {
                    // Crear lista con los correos electrónicos de ambos usuarios
                    List<String> userEmails = new ArrayList<>();
                    userEmails.add(currentUserEmail);
                    userEmails.add(email);

                    db.collection("friends")
                            .whereIn("friend_1", userEmails)
                            .whereIn("friend_2", userEmails)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    document.getReference().delete()
                                            .addOnSuccessListener(aVoid -> {
                                                // Remove friend from layout
                                                containerFriends.removeView(friendView);
                                                Toast.makeText(getActivity(), "Amigo eliminado", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "Error al eliminar amigo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Error al cargar los amigos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });

                // Configurar los datos
                textView.setText(userName);

                // Agregar amigo al contenedor
                containerFriends.addView(friendView);
            }
        });
    }

    // Método para obtener y mostrar el nombre de usuario en el EditText
    private void setUserNameInEditText() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Obtener la referencia al documento de usuario en Firestore
            DocumentReference userRef = db.collection("users").document(userId);

            // Obtener el nombre de usuario del documento de usuario
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            editTextName.setText(username);
                        } else {
                            Log.d("TAG", "El documento del usuario no existe");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TAG", "Error al obtener el nombre de usuario: " + e.getMessage());
                    });
        }
    }

    // Método para actualizar el nombre de usuario en la base de datos
    private void updateUserName(String newName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            // Consultar el documento de usuario correspondiente al correo electrónico actual
            db.collection("users")
                    .whereEqualTo("email", currentUserEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Actualizar el campo de nombre con el nuevo valor
                            documentSnapshot.getReference().update("username", newName)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getActivity(), "Nombre de usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Error al actualizar el nombre de usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Error al buscar el usuario en la base de datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getActivity(), "Usuario actual nulo", Toast.LENGTH_SHORT).show();
        }
    }
}