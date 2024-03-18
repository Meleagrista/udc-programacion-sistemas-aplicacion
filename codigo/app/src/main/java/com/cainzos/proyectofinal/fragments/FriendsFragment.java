package com.cainzos.proyectofinal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.cainzos.proyectofinal.R;
import com.cainzos.proyectofinal.fragments_friends.ListFriendsFragment;
import com.cainzos.proyectofinal.fragments_friends.ListPendingFragment;

public class FriendsFragment extends Fragment {

    private View rootView;
    private Button friendsButton;
    private Button pendingButton;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsButton = rootView.findViewById(R.id.friendsButton);
        pendingButton = rootView.findViewById(R.id.pendingList);

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ListFriendsFragment());
            }
        });

        pendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ListPendingFragment());
            }
        });

        return rootView;
    }

    private void loadFragment(Fragment fragment) {
        // Cargar el fragmento dado en el contenedor del fragmento
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
