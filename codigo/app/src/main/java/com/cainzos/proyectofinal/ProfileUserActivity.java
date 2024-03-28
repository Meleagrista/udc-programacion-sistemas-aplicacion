package com.cainzos.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.cainzos.proyectofinal.databinding.ActivityProfileUserBinding;
import com.cainzos.proyectofinal.recursos.managers.UserDataManager;
import com.cainzos.proyectofinal.recursos.objects.User;
import com.google.firebase.auth.FirebaseUser;

public class ProfileUserActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityProfileUserBinding binding;
    EditText nameEditText, extraEditText;
    Button editExtraFieldButton, editUsernameButton;

    FirebaseUser currentUser;
    private UserDataManager userDataManager;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDataManager = UserDataManager.getInstance();
        currentUser = userDataManager.getFirebaseUser();

        nameEditText = binding.UserNameEditText;
        nameEditText.setEnabled(false);
        extraEditText = binding.UserExtraFieldEditText;
        extraEditText.setEnabled(false);

        if(!currentUser.isAnonymous()){
            user = userDataManager.getUser();
            nameEditText.setText(user.getUserName());
        }

        editUsernameButton = binding.editUserName;
        editExtraFieldButton = binding.editExtraField;
        editUsernameButton.setOnClickListener(this);
        editExtraFieldButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == editExtraFieldButton){
            if(currentUser.isAnonymous()){
                Toast.makeText(this, "Inicia sesion para modificar el extra", Toast.LENGTH_SHORT).show();
            }else{
                if(!extraEditText.isEnabled()) {
                    extraEditText.setEnabled(true);
                    Toast.makeText(this, "Editing extra is now allowed", Toast.LENGTH_SHORT).show();
                } else {
                    extraEditText.setEnabled(false);
                }
            }
        } else if (v == editUsernameButton){
            if(currentUser.isAnonymous()){
                Toast.makeText(this, "Inicia sesion para modificar el nombre de usuario", Toast.LENGTH_SHORT).show();
            }else{
                if(!nameEditText.isEnabled()) {
                    nameEditText.setEnabled(true);
                    Toast.makeText(this, "Editing username is now allowed", Toast.LENGTH_SHORT).show();
                } else {
                    nameEditText.setEnabled(false);
                }
            }
        }
    }
}