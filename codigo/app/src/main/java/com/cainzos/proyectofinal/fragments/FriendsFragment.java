package com.cainzos.proyectofinal.fragments;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FriendsFragment extends Fragment {

    private FragmentFriendsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        binding = FragmentFriendsBinding.inflate(getLayoutInflater());

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        loadUserId();

        binding.friendsButton.setOnClickListener(v -> loadFragment(new ListFriendsFragment()));
        binding.pendingList.setOnClickListener(v -> loadFragment(new ListPendingFragment()));

        return binding.getRoot();
    }

    /*--- Función para cargar el ID del usuario en el TextView ---*/
    private void loadUserId() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && !mAuth.getCurrentUser().isAnonymous()) {
            String userId = currentUser.getUid();
            mFirestore.collection(getString(R.string.collection_path_users)).document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String userID = document.getString("id");
                                binding.idUser.setText(userID);
                            }
                        }
                    });
        } else {
            //En el caso de ser usuario anónimo se mostrara esto
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
