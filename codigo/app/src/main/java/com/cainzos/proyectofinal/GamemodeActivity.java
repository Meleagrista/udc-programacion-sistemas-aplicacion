package com.cainzos.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.cainzos.proyectofinal.databinding.ActivityGamemodeBinding;
import com.cainzos.proyectofinal.fragments.FriendsFragment;
import com.cainzos.proyectofinal.fragments.GamemodeFragment;
import com.cainzos.proyectofinal.fragments.ShopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GamemodeActivity extends AppCompatActivity {

    private ActivityGamemodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGamemodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*---Valor de los parametros cuando se inicia la actividad---*/
        replaceFragment(new GamemodeFragment()); //Fragmento de inicio
        binding.bottomNavigationView.setSelectedItemId(R.id.nav_item2); //Boton seleccionado de inicio

        /*---Gestion de los distintos botones de la bottom Navigation View---*/
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if(id == R.id.nav_item1){
                replaceFragment(new FriendsFragment());
            }else if(id == R.id.nav_item2){
                replaceFragment(new GamemodeFragment());
            }else if(id == R.id.nav_item3){
                replaceFragment(new ShopFragment());
            }
            return true;
        });
    }

    /*---Funcion que se encarga de cambiar el fragmento que se ve---*/
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
