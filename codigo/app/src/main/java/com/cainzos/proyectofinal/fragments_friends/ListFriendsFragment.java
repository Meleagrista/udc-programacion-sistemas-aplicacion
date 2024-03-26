package com.cainzos.proyectofinal.fragments_friends;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cainzos.proyectofinal.R;
import com.cainzos.proyectofinal.databinding.ActivityListFriendsFragmentBinding;
import com.cainzos.proyectofinal.databinding.FriendItemBinding;
import com.cainzos.proyectofinal.recursos.User;
import com.cainzos.proyectofinal.recursos.UserDataManager;
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
        // Disabling name editing initially
        binding.editTextName.setEnabled(false);

        // Setting current user's name in EditText
        userDataManager = UserDataManager.getInstance();
        currentUser = userDataManager.getFirebaseUser();
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
                userDataManager.updateUserName(newName, currentUser.getEmail(), getActivity());
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
        List<User> friends = userDataManager.getFriends();
        // Check if user data manager has finished loading user data
        if (userDataManager.getUser() != null) {
            // User data is loaded, load friends list
            friends.forEach(this::addFriendToLayout);
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

    // Method to set current user's name in EditText
    private void setUserNameInEditText() {
        User user = userDataManager.getUser();
        if (currentUser != null) {
            binding.editTextName.setText(user.getUserName());
        } else {
            binding.editTextName.setText(R.string.anonymous);
        }
    }
}