package com.cainzos.proyectofinal.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.cainzos.proyectofinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class FriendsFragment extends Fragment {

    private EditText editTextEmail, editTextName;
    private Button buttonSendRequest, buttonEditName;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private View rootView;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        // Obtener instancia de FirebaseFirestore y FirebaseAuth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Obtener referencias a los elementos de interfaz de usuario
        editTextEmail = rootView.findViewById(R.id.editTextEmail);
        buttonSendRequest = rootView.findViewById(R.id.buttonSendRequest);
        editTextName = rootView.findViewById(R.id.editTextName);
        buttonEditName = rootView.findViewById(R.id.editUserName);
        editTextName.setEnabled(false);

        // Obtener el nombre de usuario de Firestore y establecerlo en el EditText
        setUserNameInEditText();

        // Lógica para enviar solicitudes de amistad al hacer clic en el botón
        buttonSendRequest.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String userName = editTextName.getText().toString().trim();

            // Verificar que se haya ingresado un correo electrónico válido
            if (!isValidEmail(email)) {
                Toast.makeText(getActivity(), "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show();
                return;
            }
            sendFriendRequest(email, userName);
        });

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

        // Cargar las solicitudes de amistad recibidas
        loadFriendRequests();

        return rootView;
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

    // Método para verificar si un correo electrónico es válido
    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Método para enviar la solicitud de amistad
    private void sendFriendRequest(String email, String userName) {

        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isAnonymous()) {
            // El usuario ha iniciado sesión de forma anónima, mostrar un mensaje de error
            Toast.makeText(getActivity(), "No puedes enviar solicitudes de amistad mientras estás registrado como usuario anónimo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa para almacenar la solicitud de amistad
        Map<String, Object> friendRequest = new HashMap<>();
        friendRequest.put("sender_email", mAuth.getCurrentUser().getEmail()); // Aquí pasamos el correo del remitente
        friendRequest.put("receiver_email", email);
        friendRequest.put("status", "pending"); // Indica que la solicitud está pendiente
        friendRequest.put("user_name", userName);

        // Almacenar la solicitud de amistad en la base de datos
        db.collection("friend_requests")
                .add(friendRequest)
                .addOnSuccessListener(documentReference -> Toast.makeText(getActivity(), "Solicitud de amistad enviada a: " + email, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al enviar la solicitud de amistad: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    // Método para cargar las solicitudes de amistad recibidas
    private void loadFriendRequests() {
        db.collection("friend_requests")
                .whereEqualTo("receiver_email", mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        LinearLayout container = rootView.findViewById(R.id.containerFriendRequests);
                        container.removeAllViews();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String senderEmail = document.getString("sender_email");
                            String status = document.getString("status");

                            if (status.equals("pending")) {
                                // Inflar el diseño de la solicitud de amistad
                                View requestView = LayoutInflater.from(getActivity()).inflate(R.layout.friend_request_item, container, false);

                                // Obtener referencias a las vistas dentro del diseño inflado
                                TextView textViewSenderEmail = requestView.findViewById(R.id.textViewSenderEmail);
                                Button buttonAccept = requestView.findViewById(R.id.buttonAccept);
                                Button buttonReject = requestView.findViewById(R.id.buttonReject);

                                // Establecer el correo electrónico del remitente
                                textViewSenderEmail.setText(senderEmail);

                                // Establecer OnClickListener para el botón Aceptar
                                buttonAccept.setOnClickListener(v -> {
                                    document.getReference().update("status", "accepted")
                                            .addOnSuccessListener(aVoid -> {
                                                container.removeView(requestView);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "Error al aceptar la solicitud de amistad", Toast.LENGTH_SHORT).show();
                                            });
                                });

                                // Establecer OnClickListener para el botón Rechazar
                                buttonReject.setOnClickListener(v -> {
                                    document.getReference().update("status", "rejected")
                                            .addOnSuccessListener(aVoid -> {
                                                container.removeView(requestView);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "Error al rechazar la solicitud de amistad", Toast.LENGTH_SHORT).show();
                                            });
                                });

                                // Agregar la vista de la solicitud de amistad al contenedor
                                container.addView(requestView);
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al cargar las solicitudes de amistad: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}