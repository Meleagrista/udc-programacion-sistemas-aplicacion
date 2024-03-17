package com.cainzos.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.cainzos.proyectofinal.databinding.ActivityGamemodeBinding;
import com.cainzos.proyectofinal.fragments.FriendsFragment;
import com.cainzos.proyectofinal.fragments.GamemodeFragment;
import com.cainzos.proyectofinal.fragments.ShopFragment;

public class GamemodeActivity extends AppCompatActivity {

    private ActivityGamemodeBinding binding;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGamemodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*---Inicializamos la toolbar---*/
        Toolbar toolbar = binding.toolbar;
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        /*---Valor de los parametros cuando se inicia la actividad---*/
        if (savedInstanceState == null) {
            replaceFragment(new GamemodeFragment(), "gamemode");
            binding.bottomNavigationView.setSelectedItemId(R.id.gamemode_item); //Boton seleccionado de inicio
        }

        /*---Gestion de los distintos botones de la bottom Navigation View---*/
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if(id == R.id.friends_item){
                replaceFragment(new FriendsFragment(), "friends");
            }else if(id == R.id.gamemode_item){
                replaceFragment(new GamemodeFragment(), "gamemode");
            }else if(id == R.id.shop_item){
                replaceFragment(new ShopFragment(), "shop");
            }
            return true;
        });
    }

    /*---Funcion que se encarga de cambiar el fragmento que se ve---*/
    private void replaceFragment(Fragment fragment, String tag) {
        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (existingFragment != null) {
            // Si el fragmento ya está presente, lo muestra
            fragmentTransaction.show(existingFragment);
        } else {
            // Si el fragmento no está presente, lo añade al contenedor
            fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
        }

        // Oculta otros fragmentos en la transacción
        for (Fragment fragmentInStack : fragmentManager.getFragments()) {
            if (fragmentInStack != null && !fragmentInStack.getTag().equals(tag)) {
                fragmentTransaction.hide(fragmentInStack);
            }
        }

        fragmentTransaction.commit();
    }
}
