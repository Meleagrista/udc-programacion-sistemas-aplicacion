package com.cainzos.proyectofinal.recursos.managers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.cainzos.proyectofinal.recursos.objects.FriendRequest;
import com.cainzos.proyectofinal.recursos.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserDataManager {

    private static UserDataManager instance;
    //Variables de gestion de firebase
    private final FirebaseUser currentUser;
    private final FirebaseFirestore mFirestore;
    //Variables de datos de usuario
    private User user;
    private final List<User> friends = new ArrayList<>();
    private final List<FriendRequest> myRequests = new ArrayList<>();
    private final List<FriendRequest> sentRequests = new ArrayList<>();

    //Constructor
    private UserDataManager() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        if (currentUser != null) {
            // Llama a loadUserData con un callback para manejar los datos del usuario una vez cargados
            loadUserData(loadedUser -> {
                user = loadedUser;
                Log.d("_TAG", "Datos del usuario: " + user.getUserName());
                // Una vez que los datos del usuario se hayan cargado, carga las otras partes de la información del usuario
                loadFriends(() -> {
                    loadMyFriendRequests();
                    loadSentFriendRequests();
                });
            });
        }
    }

    /*---GESTION DE INSTANCIA---*/

    // Método para crear la instancia de UserDataManager o en caso de ya existir, devolverla
    public static synchronized UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    // Método para borrar la instancia actual de UserDataManager
    public static synchronized void clearInstance() {
        instance = null;
    }

    /*---INTERFACES CALLBACK---*/

    public interface OnUserLoadedCallback {
        void onUserLoaded(User user);
    }

    // Interfaz de callback para la carga de amigos
    public interface OnFriendsLoadedCallback {
        void onFriendsLoaded();
    }

    /*---GESTION USUARIOS---*/

    public FirebaseUser getFirebaseUser(){
        return currentUser;
    }

    public User getUser(){
        return user;
    }

    public void updateUserName(String newName, String currentUserEmail, Activity activity) {
        if (currentUserEmail != null) {
            mFirestore.collection("users")
                    .whereEqualTo("email", currentUserEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            user.setUserName(newName);
                            // Update the username field with the new value
                            documentSnapshot.getReference().update("username", newName)
                                    .addOnSuccessListener(aVoid ->
                                            // Show success message when username is updated
                                            Toast.makeText(activity, "Nombre de usuario actualizado correctamente", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            // Show error message if username update fails
                                            Toast.makeText(activity, "Error al actualizar el nombre de usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    })
                    .addOnFailureListener(e ->
                            // Show error message if user search in the database fails
                            Toast.makeText(activity, "Error al buscar el usuario en la base de datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else if(currentUser == null){
            // Show error message if current user email is null
            Toast.makeText(activity, "Correo electrónico del usuario nulo", Toast.LENGTH_SHORT).show();
        }else if(Objects.equals(newName, user.getUserName())){
            Toast.makeText(activity, "El usuario introducido es el mismo que ya tienes", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData(OnUserLoadedCallback callback) {
        mFirestore.collection("users")
                .whereEqualTo("email", currentUser.getEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Obtener el primer documento encontrado (debería haber solo uno)
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String userId = documentSnapshot.getString("id");
                        String userEmail = documentSnapshot.getString("email");
                        String userName = documentSnapshot.getString("username");
                        String tag = documentSnapshot.getString("tag");
                        String password = documentSnapshot.getString("password");

                        // Crear una instancia de User con los datos del usuario actual
                        user = new User(userId, userEmail, userName, tag, password);

                        // Llamar al callback con el usuario cargado
                        callback.onUserLoaded(user);
                        Log.d("UserDataManager", "Se encontró documento con el correo electrónico del usuario: " + currentUser.getEmail());
                    } else {
                        // Llamar al callback con null si no se encuentra ningún documento
                        callback.onUserLoaded(null);
                        // Manejar el caso en el que no se encuentre ningún documento con el correo electrónico del usuario actual
                        Log.d("UserDataManager", "No se encontró ningún documento con el correo electrónico del usuario: " + currentUser.getEmail());
                    }
                })
                .addOnFailureListener(e -> {
                    // Llamar al callback con null en caso de error
                    callback.onUserLoaded(null);
                    // Manejar el caso de error, si es necesario
                    Log.d("UserDataManager", "Error al cargar los datos del usuario: " + e.getMessage());
                });
    }

    /*---GESTION AMIGOS---*/

    private void getUserByEmail(String email, OnUserLoadedCallback callback){
        mFirestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task ->{
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener los datos del documento y crear un objeto User
                            String userId = document.getString("id");
                            String userEmail = document.getString("email");
                            String userName = document.getString("username");
                            String tag = document.getString("tag");
                            String password = document.getString("password");

                            User friend = new User(userId, userEmail, userName, tag, password);

                            // Llamar al método callback con el usuario cargado
                            callback.onUserLoaded(friend);
                        }
                    }
                });
    }

    public List<User> getFriends() {
        return friends;
    }

    // Método para cargar la lista de amigos del usuario
    private void loadFriends(OnFriendsLoadedCallback callback) {
        // Comprobar si currentUser no es nulo
        if (currentUser != null) {
            // Consulta para buscar si el currentUser está en algún campo friend_1
            mFirestore.collection("friends_request")
                    .whereEqualTo("receiver_email", user.getUserEmail())
                    .whereEqualTo("status", "accepted")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            // El currentUser está en algún campo friend_1
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String friendEmail = document.getString("sender_email");
                                // Añadir el correo electrónico del amigo a la lista de amigos
                                Log.d("UserDataManager", "Añadido amigo con correo: " + friendEmail);
                                addFriendByEmail(friendEmail);
                            }
                        }
                        // Llamar al callback una vez cargada la lista de amigos
                        callback.onFriendsLoaded();
                    })
                    .addOnFailureListener(e -> {
                        // Manejar error en la consulta de friend_1
                    });

            // El currentUser no está en ningún campo friend_1, buscar en friend_2
            mFirestore.collection("friends_request")
                    .whereEqualTo("sender_email", currentUser.getEmail())
                    .whereEqualTo("status", "accepted")
                    .get()
                    .addOnSuccessListener(querySnapshot2 -> {
                        if (!querySnapshot2.isEmpty()) {
                            // El currentUser está en algún campo friend_2
                            for (QueryDocumentSnapshot document : querySnapshot2) {
                                String friendEmail = document.getString("receiver_email");
                                // Añadir el correo electrónico del amigo a la lista de amigos
                                Log.d("UserDataManager", "Añadido amigo con correo: " + friendEmail);
                                addFriendByEmail(friendEmail);
                            }
                        }
                        // Llamar al callback una vez cargada la lista de amigos
                        callback.onFriendsLoaded();
                    })
                    .addOnFailureListener(e -> {
                        // Manejar error en la consulta de friend_2
                    });

        }
    }

    // Método para agregar un amigo por su correo electrónico a la lista de amigos
    private void addFriendByEmail(String friendEmail) {
        getUserByEmail(friendEmail, friend -> {
            if (friend != null) {
                // Añadir el amigo a la lista de amigos
                friends.add(friend);
            }
        });
    }

    public void addFriend(User user){
        friends.add(user);
    }

    //Funcion para borrar un amigo de la lista y de la base de datos
    public void deleteFriend(User user){
        friends.remove(user);

        // Obtener el correo electrónico del amigo que se va a eliminar
        String friendEmail = user.getUserEmail();

        // Realizar una consulta para buscar el documento que representa la amistad entre el usuario actual y el amigo que se va a eliminar
        mFirestore.collection("friends_request")
                .whereEqualTo("receiver_email", user.getUserEmail())
                .whereEqualTo("sender_email", friendEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Eliminar el documento de Firestore
                        myRequests.remove(getSentFriendRequestByEmail(friendEmail));
                        mFirestore.collection("friends_request").document(document.getId()).delete();
                    }
                });

        // Realizar una consulta para buscar el documento que representa la amistad entre el usuario actual y el amigo que se va a eliminar
        mFirestore.collection("friends_request")
                .whereEqualTo("receiver_email", friendEmail)
                .whereEqualTo("sender_email", user.getUserEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Eliminar el documento de Firestore
                        sentRequests.remove(getMyFriendRequestByEmail(friendEmail));
                        mFirestore.collection("friends_request").document(document.getId()).delete();
                    }
                });
    }

    /*---GESTION SOLICITUDES---*/

    //Getter de las solicitudes enviadas por el usuario actual
    public FriendRequest getSentFriendRequestByEmail(String email) {
        for (FriendRequest request : sentRequests) {
            if (request.getUser().getUserEmail().equals(email)) {
                return request;
            }
        }
        return null;
    }

    //Getter de las solicitudes recibidas por el usuario actual
    public FriendRequest getMyFriendRequestByEmail(String email) {
        for (FriendRequest request : myRequests) {
            if (request.getUser().getUserEmail().equals(email)) {
                return request;
            }
        }
        return null;
    }

    // Método para cargar las solicitudes dependiendo de si las ha enviado o recibido el usuario
    private void loadSentAndReceivedFriendRequests(boolean sent) {
        String field = sent ? "sender_email" : "receiver_email";
        mFirestore.collection("friends_request")
                .whereEqualTo(field, currentUser.getEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<FriendRequest> requestsList = sent ? sentRequests : myRequests;
                    requestsList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String email = sent ? document.getString("receiver_email") : document.getString("sender_email");
                        String status = document.getString("status");
                        // Utilizamos getUserData para cargar los datos completos del remitente o del receptor, según corresponda
                        getUserByEmail(email, user -> {
                            // Creamos la solicitud de amistad con los datos del remitente cargados;
                            FriendRequest friendRequest = new FriendRequest(user, status);
                            requestsList.add(friendRequest);
                        });
                    }
                });
    }

    //Metodo auxiliar para cargar solicitudes enviadas
    private void loadSentFriendRequests() {
        loadSentAndReceivedFriendRequests(true);
    }

    //Metodo auxiliar para cargar solicitudes recibidas
    private void loadMyFriendRequests() {
        loadSentAndReceivedFriendRequests(false);
    }

    //Getter de las solicitudes recibidas
    public List<FriendRequest> getMyFriendRequests() {
        return myRequests;
    }

    //Metodo para crear una solicitud de amistad
    public void createFriendRequest(String email, Activity activity) {
        Map<String, Object> friendRequestData = new HashMap<>();
        friendRequestData.put("sender_email", user.getUserEmail());
        friendRequestData.put("receiver_email", email);
        friendRequestData.put("status", "pending");

        getUserByEmail(email, friend ->{
            sentRequests.add(new FriendRequest(friend, "pending"));
        });

        // Add friend request to Firestore
        mFirestore.collection("friends_request")
                .add(friendRequestData)
                .addOnSuccessListener(task-> Toast.makeText(activity, "Solicitud de amistad enviada correctamente", Toast.LENGTH_SHORT).show());
    }

    //Metodo para modificar el estado de una solicitud de amistad
    public void updateFriendRequestStatus(FriendRequest friendRequest, String newStatus) {
        // Update status in the local list
        friendRequest.setStatus(newStatus);

        // Update status in Firestore
        mFirestore.collection("friends_request")
                .whereEqualTo("sender_email", friendRequest.getUser().getUserEmail())
                .whereEqualTo("receiver_email", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Update status to Firestore document
                            document.getReference().update("status", newStatus)
                                    .addOnSuccessListener(aVoid -> {
                                        // Show success message or handle success event if needed
                                    })
                                    .addOnFailureListener(e -> {
                                        // Show error message or handle failure event if needed
                                    });
                        }
                    }
                });
    }
}
