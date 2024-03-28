package com.cainzos.proyectofinal.fragments_friends;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cainzos.proyectofinal.R;
import com.cainzos.proyectofinal.databinding.ActivityListFriendsFragmentBinding;
import com.cainzos.proyectofinal.databinding.FriendItemBinding;
import com.cainzos.proyectofinal.recursos.objects.User;
import com.cainzos.proyectofinal.recursos.managers.UserDataManager;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class ListFriendsFragment extends Fragment {

    //Bindings
    private ActivityListFriendsFragmentBinding binding; // View binding for this fragment
    private FriendItemBinding friends_binding; // View binding for friends' items
    //Variables de gestion de datos
    UserDataManager userDataManager;
    //Variables de firebase
    FirebaseUser currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating layout
        binding = ActivityListFriendsFragmentBinding.inflate(getLayoutInflater());
        // Inflating friend item layout
        friends_binding = FriendItemBinding.inflate(getLayoutInflater());

        // Setting current user's name in EditText
        userDataManager = UserDataManager.getInstance();
        currentUser = userDataManager.getFirebaseUser();

        // Loading friends list
        loadFriends();

        return binding.getRoot();
    }

    // Method to load user's friends list
    private void loadFriends() {
        List<User> friends = userDataManager.getFriends();
        // Check if user data manager has finished loading user data
        if (userDataManager.getUser() != null) {
            if(currentUser.isAnonymous()){
                Toast.makeText(getActivity(), "Inicia sesion para poder añadir amigos", Toast.LENGTH_SHORT).show();
            }else{
                // User data is loaded, load friends list
                friends.forEach(this::addFriendToLayout);
            }
        }
    }

    // Method to add a friend to the layout
    private void addFriendToLayout(User friend) {
        // Inflate friend item layout
        View friendItemView = LayoutInflater.from(getContext()).inflate(R.layout.friend_item, binding.containerFriends, false);
        // Get references to views in friend item layout
        TextView textViewFriendName = friendItemView.findViewById(R.id.textViewFriendName);
        Button buttonDelete = friendItemView.findViewById(R.id.buttonDelete);

        // Set friend's information in views
        String userName = friend.getUserName();
        String tag = friend.getTag();

        if (userName == null || userName.isEmpty()) {
            userName = "Anonymous123";
        }

        // Set friend's name in TextView
        String text = userName + " " + tag;
        textViewFriendName.setText(text);
        // Set the text color
        int textColorResId = R.color.white;
        int textColor = ContextCompat.getColor(requireContext(), textColorResId);
        textViewFriendName.setTextColor(textColor);

        // Handle click event on delete friend button
        buttonDelete.setOnClickListener(view -> {
            // Remove friend view from layout
            binding.containerFriends.removeView(friendItemView);
            userDataManager.deleteFriend(friend);
        });

        // Add friend view to friends container
        binding.containerFriends.addView(friendItemView);
    }
}