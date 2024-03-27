package com.cainzos.proyectofinal.fragments_menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cainzos.proyectofinal.R;
import com.cainzos.proyectofinal.databinding.FragmentFriendsBinding;
import com.cainzos.proyectofinal.fragments_friends.ListFriendsFragment;
import com.cainzos.proyectofinal.fragments_friends.ListPendingFragment;
import com.cainzos.proyectofinal.recursos.managers.UserDataManager;
import com.google.firebase.auth.FirebaseUser;

public class FriendsFragment extends Fragment {

    private FragmentFriendsBinding binding;
    UserDataManager userDataManager;
    FirebaseUser currentUser;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        binding = FragmentFriendsBinding.inflate(getLayoutInflater());
        userDataManager = UserDataManager.getInstance();

        loadUserId();

        binding.friendsButton.setOnClickListener(v -> loadFragment(new ListFriendsFragment()));
        binding.pendingList.setOnClickListener(v -> loadFragment(new ListPendingFragment()));

        return binding.getRoot();
    }

    /*--- Función para cargar el ID del usuario en el TextView ---*/
    private void loadUserId() {
        currentUser = userDataManager.getFirebaseUser();
        if (currentUser != null && !userDataManager.getFirebaseUser().isAnonymous()) {
            String userId = currentUser.getUid();
            binding.idUser.setText(userId);
        } else {
            // En el caso de ser usuario anónimo, se mostrará este mensaje
            binding.idUser.setText(R.string.login_toget_id_msg);
        }
    }


    /*---Función para gestionar los fragmentos---*/
    private void loadFragment(Fragment fragment) {

        // Cargar el fragmento dado en el contenedor del fragmento
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
