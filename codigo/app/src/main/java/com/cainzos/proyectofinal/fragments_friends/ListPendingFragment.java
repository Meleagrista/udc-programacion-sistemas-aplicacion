package com.cainzos.proyectofinal.fragments_friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cainzos.proyectofinal.R;
import com.cainzos.proyectofinal.databinding.ActivityListPendingFragmentBinding;
import com.cainzos.proyectofinal.databinding.FriendRequestItemBinding;
import com.cainzos.proyectofinal.recursos.managers.DatabaseManager;
import com.cainzos.proyectofinal.recursos.objects.FriendRequest;
import com.cainzos.proyectofinal.recursos.objects.User;
import com.cainzos.proyectofinal.recursos.managers.UserDataManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

public class ListPendingFragment extends Fragment {

    //Bindings
    private ActivityListPendingFragmentBinding binding; // View binding for this fragment
    private FriendRequestItemBinding friends_binding; // View binding for friend request items

    //Variables de gestion de datos
    private DatabaseManager databaseManager;
    private UserDataManager userDataManager;
    //Variables de firebase
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityListPendingFragmentBinding.inflate(getLayoutInflater()); // Inflate the layout for this fragment
        friends_binding = FriendRequestItemBinding.inflate(getLayoutInflater());

        //Obtencion de instancias
        databaseManager = DatabaseManager.getInstance();
        userDataManager = UserDataManager.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Establecer el listener para el boton de enviar solicitudes de amistad
        binding.buttonSendRequest.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();

            if (!isValidEmail(email)) { //Validar formato de email
                Toast.makeText(getActivity(), "Por favor, ingresa un correo electrónico valido", Toast.LENGTH_SHORT).show();
                return;
            }
            sendFriendRequest(email); //Metodo para enviar solicitud de amistad
        });

        loadFriendRequestsList();

        return binding.getRoot(); // Return the root view
    }

    // Method to validate email format
    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Method to send friend request
    private void sendFriendRequest(String email) {

        // Prevent sending friend requests while logged in as anonymous user
        if (currentUser != null && currentUser.isAnonymous()) {
            Toast.makeText(getActivity(), "No puedes enviar solicitudes de amistad mientras estás registrado como usuario anónimo", Toast.LENGTH_SHORT).show();
            return;
        }
        // Prevent sending friend requests to oneself
        if(currentUser != null && Objects.equals(currentUser.getEmail(), email)){
            Toast.makeText(getActivity(), "No puedes enviarte solicitudes de amistad a ti mismo", Toast.LENGTH_SHORT).show();
            return;
        }
        checkUserExists(email); // Check if the user exists and proceed accordingly
    }

    // Método para comprobar si el usuario existe
    private void checkUserExists(String email) {
        databaseManager.getAllUsers(getActivity(), users -> {
            boolean userExists = false;

            //We check if the user exists on the database
            for (User user : users) {
                if (user.getUserEmail().equals(email)) {
                    userExists = true;
                    break;
                }
            }

            //If the user exists on the database
            if (userExists) {
                FriendRequest myRequest = userDataManager.getMyFriendRequestByEmail(email);
                FriendRequest sentRequest = userDataManager.getSentFriendRequestByEmail(email);
                if(sentRequest != null){
                    Toast.makeText(getActivity(), "Ya has enviado una solicitud a este usuario", Toast.LENGTH_SHORT).show();
                }else if(myRequest != null){
                    Toast.makeText(getActivity(), "Ya tienes una solicitud pendiente de este usuario", Toast.LENGTH_SHORT).show();
                }else{
                    userDataManager.createFriendRequest(email, getActivity());
                }
            } else { //If the user doesn't exist on the database
                Toast.makeText(getActivity(), "El usuario con el correo electrónico especificado no existe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method to load the requests that the user received
    private void loadFriendRequestsList() {
        LinearLayout container = binding.containerFriendRequests;
        container.removeAllViews(); // Remove all views from the container

        for (FriendRequest friendRequest : userDataManager.getMyFriendRequests()) {
            String senderEmail = friendRequest.getUser().getUserEmail();
            String status = friendRequest.getStatus();

            assert status != null;
            if (status.equals("pending")) {
                // Inflate layout for friend request
                View friendRequestView = LayoutInflater.from(container.getContext()).inflate(R.layout.friend_request_item, container, false);
                TextView textViewSenderEmail = friendRequestView.findViewById(R.id.textViewSenderEmail);
                Button buttonAccept = friendRequestView.findViewById(R.id.buttonAccept);
                Button buttonReject = friendRequestView.findViewById(R.id.buttonReject);

                // Set sender's email in text view
                textViewSenderEmail.setText(senderEmail);

                buttonAccept.setOnClickListener(v -> {
                    // Show snackbar with option to undo
                    Snackbar snackbar = Snackbar.make(binding.getRoot(), "Solicitud de amistad aceptada", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", v1 -> userDataManager.updateFriendRequestStatus(friendRequest, "pending"));
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                userDataManager.updateFriendRequestStatus(friendRequest, "accepted");
                                userDataManager.addFriend(friendRequest.getUser());
                            }
                        }
                    });
                    snackbar.show();
                    container.removeView(friendRequestView); // Remove the view of the request from the container
                });

                buttonReject.setOnClickListener(v -> {
                    // Show snackbar with option to undo
                    Snackbar snackbar = Snackbar.make(binding.getRoot(), "Solicitud de amistad rechazada", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", v1 -> userDataManager.updateFriendRequestStatus(friendRequest, "pending"));
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                userDataManager.updateFriendRequestStatus(friendRequest, "rejected");
                                userDataManager.deleteFriend(friendRequest.getUser());
                            }
                        }
                    });
                    snackbar.show();
                    container.removeView(friendRequestView); // Remove the view of the request from the container
                });

                container.addView(friendRequestView); // Add the view of the request to the container
            }
        }
    }

}