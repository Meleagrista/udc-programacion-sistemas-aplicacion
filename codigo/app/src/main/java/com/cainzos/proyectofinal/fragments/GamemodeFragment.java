package com.cainzos.proyectofinal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
        // Constructor vacío
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos el layout de este fragmento utilizando el objeto de enlace
        binding = FragmentGamemodeBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        int left = 20;
        int right = 20;

        viewPager = binding.viewPager;
        adapter = new CustomAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(left,0,right,0);
        viewPager.setOffscreenPageLimit(1);

        final float nextItemVisiblePx = getResources().getDimension(R.dimen.viewpager_next_item_visible);
        final float currentItemHorizontalMarginPx = getResources().getDimension(R.dimen.viewpager_current_item_horizontal_margin);
        final float pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx;

        ViewPager2.PageTransformer pageTransformer = new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setTranslationX(-pageTranslationX * position);
                // Escala la imagen de los previews
                page.setScaleY(1 - (0.5f * Math.abs(position)));
                page.setScaleX(1 - (0.1f * Math.abs(position)));
                // La siguiente linea se usa para darle un efecto de borroso
                page.setAlpha(0.75f + (1 - Math.abs(position)));
            }
        };
        viewPager.setPageTransformer(pageTransformer);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        return rootView;
    }
}
