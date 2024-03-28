package com.cainzos.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Objects;

public class MenuActivity extends AppCompatActivity {

    /*---Bindings---*/
    private ActivityMenuBinding binding;
    FirebaseUser currentUser;
    private UserDataManager userDataManager;
    private Fragment lastFragment;

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
            lastFragment = new GamemodeFragment();
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
                    replaceFragment(new RoomFragment(), "room");
                }
            }
            return true;
        });

    }

    /*---Funcion que se encarga de cambiar el fragmento que se ve---*/
    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        // Initialize the animation variables
        int enterAnimation = 0, exitAnimation = 0;

        // Determine which was the last fragment showed
        if (lastFragment instanceof FriendsFragment) {
            if(Objects.equals(tag, "gamemode") || Objects.equals(tag, "room")){
                enterAnimation = R.anim.slide_in_right;
                exitAnimation = R.anim.slide_out_left;
            }
        } else if (lastFragment instanceof RoomFragment) {
            if(Objects.equals(tag, "friends") || Objects.equals(tag, "gamemode")){
                enterAnimation = R.anim.slide_in_left;
                exitAnimation = R.anim.slide_out_right;
            }
        } else if(lastFragment instanceof GamemodeFragment){
            if(Objects.equals(tag, "friends")){
                enterAnimation = R.anim.slide_in_left;
                exitAnimation = R.anim.slide_out_right;
            }else if(Objects.equals(tag, "room")){
                enterAnimation = R.anim.slide_in_right;
                exitAnimation = R.anim.slide_out_left;
            }
        }

        // Execute the animations
        fragmentTransaction.setCustomAnimations(enterAnimation, exitAnimation);
        fragmentTransaction.add(R.id.fragment_container, fragment, tag);
        fragmentTransaction.commit();

        // Update the last fragment showed
        lastFragment = fragment;
    }


    /*---Inflar el menu de tres puntos---*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_nav_menu, menu);
        return true;
    }

    /*---Gestionar las acciones de los botones del menu al ser pulsados---*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("_TAG","opciones");

        if (id == R.id.nav_home){
            Log.d("_TAG", "boton home menu");
            Toast.makeText(this, "dvgejkbdxiuek", Toast.LENGTH_SHORT).show();
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
