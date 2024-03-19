package com.cainzos.proyectofinal.fragments_friends;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.cainzos.proyectofinal.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ListPendingFragment extends Fragment {

    private EditText editTextEmail;
    private Button buttonSendRequest;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_list_pending_fragment, container, false);

        // Obtener instancia de FirebaseFirestore y FirebaseAuth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Obtener referencias a los elementos de interfaz de usuario
        editTextEmail = rootView.findViewById(R.id.editTextEmail);
        buttonSendRequest = rootView.findViewById(R.id.buttonSendRequest);

        // Lógica para enviar solicitudes de amistad al hacer clic en el botón
        buttonSendRequest.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();

            // Verificar que se haya ingresado un correo electrónico válido
            if (!isValidEmail(email)) {
                Toast.makeText(getActivity(), "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show();
                return;
            }
            sendFriendRequest(email);
        });

        // Cargar las solicitudes de amistad recibidas
        loadFriendRequests();

        return rootView;
    }

    // Método para verificar si un correo electrónico es válido
    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Método para verificar si el correo del receptor existe en la colección "users"
    private void checkUserExists(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Si no se encuentra el correo en la colección "users", mostrar un mensaje de error
                            Toast.makeText(getActivity(), "El usuario con el correo electrónico especificado no existe", Toast.LENGTH_SHORT).show();
                        } else {
                            checkPendingRequest(email);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al verificar el correo del receptor: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para verificar si ya existe una solicitud de amistad pendiente entre ambos usuarios
    private void checkPendingRequest(String email) {
        db.collection("friend_requests")
                .whereEqualTo("sender_email", mAuth.getCurrentUser().getEmail())
                .whereEqualTo("receiver_email", email)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(requestTask -> {
                    if (requestTask.isSuccessful()) {
                        if (!requestTask.getResult().isEmpty()) {
                            // Si ya existe una solicitud pendiente entre ambos usuarios, mostrar un mensaje de error
                            Toast.makeText(getActivity(), "Ya has enviado una solicitud de amistad a este usuario", Toast.LENGTH_SHORT).show();
                        } else {
                            checkFriendshipStatus(email);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al verificar la solicitud de amistad pendiente: " + requestTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para verificar si ya son amigos
    private void checkFriendshipStatus(String email) {
        String currentUserEmail = mAuth.getCurrentUser().getEmail();

        // Verificar si el usuario actual está como remitente y el otro como receptor
        db.collection("friend_requests")
                .whereEqualTo("sender_email", currentUserEmail)
                .whereEqualTo("receiver_email", email)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnCompleteListener(friendTask -> {
                    if (friendTask.isSuccessful()) {
                        if (!friendTask.getResult().isEmpty()) {
                            // Si ya son amigos, mostrar un mensaje de error
                            Toast.makeText(getActivity(), "Ya eres amigo de este usuario", Toast.LENGTH_SHORT).show();
                        } else {
                            // Verificar si el usuario actual está como receptor y el otro como remitente
                            db.collection("friend_requests")
                                    .whereEqualTo("sender_email", email)
                                    .whereEqualTo("receiver_email", currentUserEmail)
                                    .whereEqualTo("status", "accepted")
                                    .get()
                                    .addOnCompleteListener(reverseFriendTask -> {
                                        if (reverseFriendTask.isSuccessful()) {
                                            if (!reverseFriendTask.getResult().isEmpty()) {
                                                // Si ya son amigos, mostrar un mensaje de error
                                                Toast.makeText(getActivity(), "Ya eres amigo de este usuario", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Crear la solicitud de amistad si ninguna de las verificaciones anteriores tiene éxito
                                                createFriendRequest(email);
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Error al verificar el estado de la amistad: " + reverseFriendTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al verificar el estado de la amistad: " + friendTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Método para crear la solicitud de amistad
    private void createFriendRequest(String email) {
        Map<String, Object> friendRequest = new HashMap<>();
        friendRequest.put("sender_email", mAuth.getCurrentUser().getEmail()); // Aquí pasamos el correo del remitente
        friendRequest.put("receiver_email", email);
        friendRequest.put("status", "pending"); // Indica que la solicitud está pendiente

        // Almacenar la solicitud de amistad en la base de datos
        db.collection("friend_requests")
                .add(friendRequest)
                .addOnSuccessListener(documentReference -> Toast.makeText(getActivity(), "Solicitud de amistad enviada a: " + email, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al enviar la solicitud de amistad: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Método para enviar la solicitud de amistad
    private void sendFriendRequest(String email) {

        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isAnonymous()) {
            // El usuario ha iniciado sesión de forma anónima, mostrar un mensaje de error
            Toast.makeText(getActivity(), "No puedes enviar solicitudes de amistad mientras estás registrado como usuario anónimo", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mAuth.getCurrentUser() != null && Objects.equals(mAuth.getCurrentUser().getEmail(), email)){
            Toast.makeText(getActivity(), "No puedes enviarte solicitudes de amistad a ti mismo", Toast.LENGTH_SHORT).show();
            return;
        }
        checkUserExists(email);
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
                                                // Mostrar Snackbar con opción "Undo" al aceptar la solicitud
                                                Snackbar snackbar = Snackbar.make(rootView, "Solicitud de amistad aceptada", Snackbar.LENGTH_LONG);
                                                snackbar.setAction("Undo", v1 -> {
                                                    // Si el usuario hace clic en "Undo", revertir la acción actualizando el estado de la solicitud a "pending"
                                                    document.getReference().update("status", "pending");
                                                    container.addView(requestView); // Añadir de nuevo la vista de la solicitud
                                                });
                                                snackbar.show();
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
                                                // Mostrar Snackbar con opción "Undo" al rechazar la solicitud
                                                Snackbar snackbar = Snackbar.make(rootView, "Solicitud de amistad rechazada", Snackbar.LENGTH_LONG);
                                                snackbar.setAction("Undo", v1 -> {
                                                    // Si el usuario hace clic en "Undo", revertir la acción actualizando el estado de la solicitud a "pending"
                                                    document.getReference().update("status", "pending");
                                                    container.addView(requestView); // Añadir de nuevo la vista de la solicitud
                                                });
                                                snackbar.show();
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