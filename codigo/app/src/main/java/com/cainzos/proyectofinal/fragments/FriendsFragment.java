package com.cainzos.proyectofinal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cainzos.proyectofinal.R;
import com.cainzos.proyectofinal.fragments_friends.ListFriendsFragment;
import com.cainzos.proyectofinal.fragments_friends.ListPendingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FriendsFragment extends Fragment {

    private View rootView;
    private Button friendsButton;
    private Button pendingButton;
    private TextView idText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsButton = rootView.findViewById(R.id.friendsButton);
        pendingButton = rootView.findViewById(R.id.pendingList);
        idText = rootView.findViewById(R.id.idUser);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        loadUserId();

        friendsButton.setOnClickListener(v -> loadFragment(new ListFriendsFragment()));

        pendingButton.setOnClickListener(v -> loadFragment(new ListPendingFragment()));

        return rootView;
    }

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
                                idText.setText(userID);
                            }
                        }
                    });
        }else{
            idText.setText(R.string.login_toget_id_msg);
        }
    }

    private void loadFragment(Fragment fragment) {
        // Cargar el fragmento dado en el contenedor del fragmento
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
