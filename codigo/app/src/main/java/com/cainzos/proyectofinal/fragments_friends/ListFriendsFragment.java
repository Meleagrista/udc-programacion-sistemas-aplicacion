package com.cainzos.proyectofinal.fragments_friends;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cainzos.proyectofinal.R;
import com.cainzos.proyectofinal.databinding.ActivityListFriendsFragmentBinding;
import com.cainzos.proyectofinal.databinding.FriendItemBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListFriendsFragment extends Fragment {
    private ActivityListFriendsFragmentBinding binding; // View binding for this fragment
    private FriendItemBinding friends_binding; // View binding for friends' items
    private FirebaseFirestore db; // Firebase Firestore instance
    private FirebaseAuth mAuth; // Firebase authentication instance

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating layout
        binding = ActivityListFriendsFragmentBinding.inflate(getLayoutInflater());
        // Inflating friend item layout
        friends_binding = FriendItemBinding.inflate(getLayoutInflater());
        // Disabling name editing initially
        binding.editTextName.setEnabled(false);

        // Initializing Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Setting current user's name in EditText
        setUserNameInEditText();

        // Handling click events on edit user name button
        binding.editUserName.setOnClickListener(v -> {
            if (!binding.editTextName.isEnabled()) {
                // Enable editing mode
                binding.editTextName.setEnabled(true);
                binding.editUserName.setText(R.string.aceptar);
            } else {
                // Save new name and disable editing mode
                String newName = binding.editTextName.getText().toString().trim();
                updateUserName(newName);
                binding.editTextName.setEnabled(false);
                binding.editUserName.setText(R.string.editar);
            }
        });

        // Loading friends list
        loadFriends();

        return binding.getRoot();
    }

    // Method to load user's friends list
    private void loadFriends() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && !mAuth.getCurrentUser().isAnonymous()) {
            String currentUserEmail = currentUser.getEmail();

            // Fetching friends where current user is the first friend
            db.collection("friends")
                    .whereEqualTo("friend_1", currentUserEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String friend2 = document.getString("friend_2");
                            assert friend2 != null;
                            if (!friend2.equals(currentUserEmail)) { addFriendToLayout(friend2); }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al cargar los amigos: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            // Fetching friends where current user is the second friend
            db.collection("friends")
                    .whereEqualTo("friend_2", currentUserEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String friend1 = document.getString("friend_1");
                            assert friend1 != null;
                            if (!friend1.equals(currentUserEmail)) { addFriendToLayout(friend1); }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al cargar los amigos: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } else if (mAuth.getCurrentUser().isAnonymous()) {
            Toast.makeText(getActivity(), "Inicia sesión para poder tener amigos", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Usuario actual nulo", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to add a friend to the layout
    private void addFriendToLayout(String email) {
        db.collection("users").whereEqualTo("email", email).get().addOnSuccessListener(receiverQueryDocumentSnapshots->{
            for (QueryDocumentSnapshot receiverDocumentSnapshot : receiverQueryDocumentSnapshots){
                String userName = receiverDocumentSnapshot.getString("username");
                if (userName == null || userName.isEmpty()) { userName = "Anonymous123"; }

                FirebaseUser currentUser = mAuth.getCurrentUser();
                assert currentUser != null;
                String currentUserEmail = currentUser.getEmail();

                // Handling click event on delete friend button
                friends_binding.buttonDelete.setOnClickListener(view -> {
                    List<String> userEmails = new ArrayList<>();
                    userEmails.add(currentUserEmail);
                    userEmails.add(email);

                    // Deleting friendship from database
                    db.collection("friends")
                            .whereIn("friend_1", userEmails)
                            .whereIn("friend_2", userEmails)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    document.getReference().delete()
                                            .addOnSuccessListener(aVoid -> {
                                                // Remove friend from layout
                                                binding.containerFriends.removeView(friends_binding.getRoot());
                                                Toast.makeText(getActivity(), "Amigo eliminado", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al eliminar amigo: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al cargar los amigos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                });

                // Setting friend's username
                friends_binding.textViewFriendName.setText(userName);

                // Adding friend item to the layout
                binding.containerFriends.addView(friends_binding.getRoot());
            }
        });
    }

    // Method to set current user's name in EditText
    private void setUserNameInEditText() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            binding.editTextName.setText(username);
                        } else {
                            Log.d("TAG", "El documento del usuario no existe");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("TAG", "Error al obtener el nombre de usuario: " + e.getMessage()));
        }
    }

    // Method to update user's name in Firestore
    private void updateUserName(String newName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            db.collection("users")
                    .whereEqualTo("email", currentUserEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Update the username field with the new value
                            documentSnapshot.getReference().update("username", newName)
                                    .addOnSuccessListener(aVoid ->
                                            // Show success message when username is updated
                                            Toast.makeText(getActivity(), "Nombre de usuario actualizado correctamente", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            // Show error message if username update fails
                                            Toast.makeText(getActivity(), "Error al actualizar el nombre de usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    })
                    .addOnFailureListener(e ->
                            // Show error message if user search in the database fails
                            Toast.makeText(getActivity(), "Error al buscar el usuario en la base de datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Show error message if current user is null
            Toast.makeText(getActivity(), "Usuario actual nulo", Toast.LENGTH_SHORT).show();
        }
    }
}