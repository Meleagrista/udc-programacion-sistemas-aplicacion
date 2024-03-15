package com.cainzos.proyectofinal.fragments;

import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class FriendsFragment extends Fragment {

    private EditText editTextEmail;
    private Button buttonSendRequest;
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

        // Cargar las solicitudes de amistad recibidas
        loadFriendRequests();

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
        return rootView;
    }

    // Método para verificar si un correo electrónico es válido
    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Método para enviar la solicitud de amistad
    private void sendFriendRequest(String email) {

        // Crear un mapa para almacenar la solicitud de amistad
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


    // Método para cargar las solicitudes de amistad recibidas
    private void loadFriendRequests() {
        // Consultar la colección friend_requests para obtener las solicitudes de amistad dirigidas al usuario actual
        db.collection("friend_requests")
                .whereEqualTo("receiver_email", mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Obtener el contenedor de solicitudes de amistad
                        LinearLayout container = rootView.findViewById(R.id.containerFriendRequests);
                        // Limpiar el contenedor antes de agregar nuevas solicitudes
                        container.removeAllViews();

                        // Iterar sobre los documentos de las solicitudes de amistad recibidas
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener el correo electrónico del remitente de la solicitud de amistad
                            String senderEmail = document.getString("sender_email");
                            String status = document.getString("status");

                            // Si la solicitud está pendiente, mostrarla en el LinearLayout
                            if (status.equals("pending")) {
                                // Crear un nuevo LinearLayout para esta solicitud
                                LinearLayout requestLayout = new LinearLayout(getActivity());
                                requestLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                requestLayout.setOrientation(LinearLayout.HORIZONTAL);

                                // TextView para mostrar el correo electrónico del remitente
                                TextView textView = new TextView(getActivity());
                                textView.setText(senderEmail);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                                textView.setLayoutParams(params);
                                requestLayout.addView(textView);

                                // Botones de aceptar y rechazar
                                Button buttonAccept = new Button(getActivity());
                                buttonAccept.setText(R.string.aceptar);
                                buttonAccept.setOnClickListener(v -> {
                                    // Lógica para aceptar la solicitud de amistad
                                    // Aquí debes implementar lo que sucede cuando se acepta la solicitud
                                });
                                requestLayout.addView(buttonAccept);

                                Button buttonReject = new Button(getActivity());
                                buttonReject.setText(R.string.rechazar);
                                buttonReject.setOnClickListener(v -> {
                                    // Lógica para rechazar la solicitud de amistad
                                    // Aquí debes implementar lo que sucede cuando se rechaza la solicitud
                                });
                                requestLayout.addView(buttonReject);

                                // Agregar el layout de la solicitud al contenedor
                                container.addView(requestLayout);
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al cargar las solicitudes de amistad: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}