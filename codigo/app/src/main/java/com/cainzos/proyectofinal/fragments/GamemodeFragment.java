package com.cainzos.proyectofinal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cainzos.proyectofinal.CustomAdapter;
import com.cainzos.proyectofinal.R;
import com.cainzos.proyectofinal.databinding.FragmentGamemodeBinding;

public class GamemodeFragment extends Fragment {

    private ViewPager2 viewPager;
    private CustomAdapter adapter;

    private FragmentGamemodeBinding binding;

    public GamemodeFragment() {
        //Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos el layout de este fragmento utilizando el objeto de enlace
        binding = FragmentGamemodeBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        viewPager = binding.viewPager;
        adapter = new CustomAdapter();
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        return rootView;
    }
}
