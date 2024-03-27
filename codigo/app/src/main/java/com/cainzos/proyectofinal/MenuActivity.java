package com.cainzos.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cainzos.proyectofinal.databinding.ActivityMenuBinding;
import com.cainzos.proyectofinal.fragments_menu.FriendsFragment;
import com.cainzos.proyectofinal.fragments_menu.GamemodeFragment;
import com.cainzos.proyectofinal.fragments_menu.RoomFragment;
import com.cainzos.proyectofinal.recursos.managers.UserDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity {

    /*---Bindings---*/
    private ActivityMenuBinding binding;
    FirebaseUser currentUser;
    private UserDataManager userDataManager;

    /*---Variable para gestionar los distintos fragmentos que se pueden mostrar---*/
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDataManager = UserDataManager.getInstance();
        currentUser = userDataManager.getFirebaseUser();

        fragmentManager = getSupportFragmentManager();

        /*---Valor de los parametros cuando se inicia la actividad---*/
        if (savedInstanceState == null) {
            replaceFragment(new GamemodeFragment(), "gamemode");
            binding.bottomNavigationView.setSelectedItemId(R.id.gamemode_item); //Boton seleccionado de inicio
        }

        /*---Gestion de los distintos botones de la bottom Navigation View---*/
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if(id == R.id.friends_item){ //Caso en el que se pulse el item de friends
                replaceFragment(new FriendsFragment(), "friends");
            }else if(id == R.id.gamemode_item){ //Caso en el que se pulse el item de gamemode
                replaceFragment(new GamemodeFragment(), "gamemode");
            }else if(id == R.id.room_item){ //Caso en el que se pulse el item de shop
                if(currentUser.isAnonymous()){
                    Toast.makeText(this, "Inicia sesion para poder unirte a salas", Toast.LENGTH_SHORT).show();
                }else{
                    replaceFragment(new RoomFragment(), "shop");
                }
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

    /*---Inflar el menu de tres puntos---*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gamemode, menu);
        return true;
    }

    /*---Gestionar las acciones de los botones del menu al ser pulsados---*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Gestionamos las distintas opciones del menu de tres puntos
        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*---Cerrar sesion en firebase---*/
    private void logout() {
        UserDataManager.clearInstance();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
