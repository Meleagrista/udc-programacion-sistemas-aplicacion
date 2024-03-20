package com.cainzos.proyectofinal.fragments_friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cainzos.proyectofinal.databinding.ActivityListPendingFragmentBinding;
import com.cainzos.proyectofinal.databinding.FriendRequestItemBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ListPendingFragment extends Fragment {

    private ActivityListPendingFragmentBinding binding; // View binding for this fragment
    private FriendRequestItemBinding friends_binding; // View binding for friend request items
    private FirebaseFirestore db; // Firebase Firestore instance
    private FirebaseAuth mAuth; // Firebase authentication instance

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityListPendingFragmentBinding.inflate(getLayoutInflater()); // Inflate the layout for this fragment
        friends_binding = FriendRequestItemBinding.inflate(getLayoutInflater());

        db = FirebaseFirestore.getInstance(); // Initialize Firebase Firestore instance
        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase authentication instance

        // Set click listener for send request button
        binding.buttonSendRequest.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();

            if (!isValidEmail(email)) { // Validate email format
                Toast.makeText(getActivity(), "Por favor, ingresa un correo electrónico valido", Toast.LENGTH_SHORT).show();
                return;
            }
            sendFriendRequest(email); // Send friend request
        });

        // Load friend requests
        loadFriendRequests();

        return binding.getRoot(); // Return the root view
    }

    // Method to validate email format
    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Method to check if user exists
    private void checkUserExists(String email) {
        // Check if user exists in the database
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getActivity(), "El usuario con el correo electrónico especificado no existe", Toast.LENGTH_SHORT).show();
                        } else {
                            checkPendingRequest(email); // Check if there's a pending request with this user
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al verificar el correo del receptor: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to check if there's a pending friend request
    private void checkPendingRequest(String email) {
        // Check if there's already a pending request sent to this user
        db.collection("friends")
                .whereEqualTo("friend_1", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                .whereEqualTo("friend_2", email)
                .get()
                .addOnCompleteListener(requestTask -> {
                    if (requestTask.isSuccessful()) {
                        if (!requestTask.getResult().isEmpty()) {
                            Toast.makeText(getActivity(), "Ya has enviado una solicitud de amistad a este usuario", Toast.LENGTH_SHORT).show();
                        } else {
                            checkFriendshipStatus(email); // Check friendship status with this user
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al verificar la solicitud de amistad pendiente: " + Objects.requireNonNull(requestTask.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to check friendship status
    private void checkFriendshipStatus(String email) {
        String currentUserEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        // Check if the user is already a friend or if there's a pending friend request in reverse direction
        db.collection("friends")
                .whereEqualTo("friend_1", currentUserEmail)
                .whereEqualTo("friend_2", email)
                .get()
                .addOnCompleteListener(friendTask -> {
                    if (friendTask.isSuccessful()) {
                        if (!friendTask.getResult().isEmpty()) {
                            Toast.makeText(getActivity(), "Ya eres amigo de este usuario", Toast.LENGTH_SHORT).show();
                        } else {
                            db.collection("friends")
                                    .whereEqualTo("friend1", email)
                                    .whereEqualTo("friend2", currentUserEmail)
                                    .get()
                                    .addOnCompleteListener(reverseFriendTask -> {
                                        if (reverseFriendTask.isSuccessful()) {
                                            if (!reverseFriendTask.getResult().isEmpty()) {
                                                Toast.makeText(getActivity(), "Ya eres amigo de este usuario", Toast.LENGTH_SHORT).show();
                                            } else {
                                                createFriendRequest(email); // Create friend request
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Error al verificar el estado de la amistad: " + Objects.requireNonNull(reverseFriendTask.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al verificar el estado de la amistad: " + Objects.requireNonNull(friendTask.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to create friend request
    private void createFriendRequest(String email) {
        Map<String, Object> friendRequest = new HashMap<>();
        friendRequest.put("sender_email", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        friendRequest.put("receiver_email", email);
        friendRequest.put("status", "pending");

        // Add friend request to Firestore
        db.collection("friend_requests")
                .add(friendRequest)
                .addOnSuccessListener(documentReference -> Toast.makeText(getActivity(), "Solicitud de amistad enviada a: " + email, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al enviar la solicitud de amistad: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Method to send friend request
    private void sendFriendRequest(String email) {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isAnonymous()) {
            // Prevent sending friend requests while logged in as anonymous user
            Toast.makeText(getActivity(), "No puedes enviar solicitudes de amistad mientras estás registrado como usuario anónimo", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mAuth.getCurrentUser() != null && Objects.equals(mAuth.getCurrentUser().getEmail(), email)){
            // Prevent sending friend requests to oneself
            Toast.makeText(getActivity(), "No puedes enviarte solicitudes de amistad a ti mismo", Toast.LENGTH_SHORT).show();
            return;
        }
        checkUserExists(email); // Check if the user exists and proceed accordingly
    }

    // Method to load friend requests
    private void loadFriendRequests() {
        db.collection("friend_requests")
                .whereEqualTo("receiver_email", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        LinearLayout container = binding.containerFriendRequests;
                        container.removeAllViews(); // Remove all views from the container

                        // Iterate through each document in the result
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String senderEmail = document.getString("sender_email");
                            String status = document.getString("status");

                            assert status != null;
                            if (status.equals("pending")) {
                                // Set sender's email in text view
                                friends_binding.textViewSenderEmail.setText(senderEmail);

                                // Set click listeners for accept and reject buttons
                                friends_binding.buttonAccept.setOnClickListener(v -> document.getReference().update("status", "accepted")
                                        .addOnSuccessListener(aVoid -> {
                                            // Add friends to Firestore when request is accepted
                                            Map<String, Object> friendsData = new HashMap<>();
                                            friendsData.put("friend_1", mAuth.getCurrentUser().getEmail());
                                            friendsData.put("friend_2", senderEmail);
                                            db.collection("friends").add(friendsData);

                                            container.removeView(friends_binding.getRoot());
                                            // Show snack-bar with option to undo
                                            Snackbar snackbar = Snackbar.make(binding.getRoot(), "Solicitud de amistad aceptada", Snackbar.LENGTH_LONG);
                                            snackbar.setAction("Undo", v1 -> {
                                                document.getReference().update("status", "pending");
                                                container.addView(friends_binding.getRoot()); // Add back the view of the request
                                            });
                                            snackbar.show();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al aceptar la solicitud de amistad", Toast.LENGTH_SHORT).show()));

                                // Set click listener for reject button
                                friends_binding.buttonReject.setOnClickListener(v -> document.getReference().update("status", "rejected")
                                        .addOnSuccessListener(aVoid -> {
                                            container.removeView(friends_binding.getRoot());
                                            // Show snack-bar with option to undo
                                            Snackbar snackbar = Snackbar.make(binding.getRoot(), "Solicitud de amistad rechazada", Snackbar.LENGTH_LONG);
                                            snackbar.setAction("Undo", v1 -> {
                                                document.getReference().update("status", "pending");
                                                container.addView(friends_binding.getRoot()); // Add back the view of the request
                                            });
                                            snackbar.show();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al rechazar la solicitud de amistad", Toast.LENGTH_SHORT).show()));

                                container.addView(friends_binding.getRoot()); // Add the view of the request to the container
                            }
                        }
                    } else {
                        // Show error message if loading friend requests fails
                        Toast.makeText(getActivity(), "Error al cargar las solicitudes de amistad: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}