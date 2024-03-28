package com.cainzos.proyectofinal;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.cainzos.proyectofinal.databinding.ActivityProfileUserBinding;
import com.cainzos.proyectofinal.recursos.managers.UserDataManager;
import com.cainzos.proyectofinal.recursos.objects.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.protobuf.Any;

public class ProfileUserActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityProfileUserBinding binding;
    EditText nameEditText, extraEditText;
    Button editExtraFieldButton, editUsernameButton;
    ImageButton profileimage;

    FirebaseUser currentUser;
    private UserDataManager userDataManager;
    private User user;

    private ActivityResultLauncher<String> pickMediaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        nameEditText = binding.UserNameEditText;
        nameEditText.setEnabled(false);
        extraEditText = binding.UserExtraFieldEditText;
        extraEditText.setEnabled(false);

        profileimage = binding.profileImage;

        userDataManager = UserDataManager.getInstance();
        currentUser = userDataManager.getFirebaseUser();
        if(!currentUser.isAnonymous()){
            user = userDataManager.getUser();
            setUserNameInEditText();
        }

        editUsernameButton = binding.editUserName;
        editExtraFieldButton = binding.editExtraField;
        editUsernameButton.setOnClickListener(this);
        editExtraFieldButton.setOnClickListener(this);
        profileimage.setOnClickListener(this);

        // Registro del lanzador de actividad en onCreate
        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                this::handleImageSelectionResult);

    }

    @Override
    public void onClick(View v) {
        if(v == editExtraFieldButton){
            if(currentUser.isAnonymous()){
                Toast.makeText(this, "Inicia sesion para modificar el extra", Toast.LENGTH_SHORT).show();
            }else{
                if (!extraEditText.isEnabled()) {
                    // Enable editing mode
                    Toast.makeText(this, "Modo edicion habilitado", Toast.LENGTH_SHORT).show();
                    extraEditText.setEnabled(true);
                } else {
                    // Save new name and disable editing mode
                    String newName = extraEditText.getText().toString().trim();
                    extraEditText.setText(newName);
                    extraEditText.setEnabled(false);
                }
            }
        } else if (v == editUsernameButton){
            if(currentUser.isAnonymous()){
                Toast.makeText(this, "Inicia sesion para modificar el nombre de usuario", Toast.LENGTH_SHORT).show();
            }else{
                if (!nameEditText.isEnabled()) {
                    // Enable editing mode
                    Toast.makeText(this, "Modo edicion habilitado", Toast.LENGTH_SHORT).show();
                    nameEditText.setEnabled(true);
                } else {
                    // Save new name and disable editing mode
                    String newName = nameEditText.getText().toString().trim();
                    userDataManager.updateUserName(newName, currentUser.getEmail(), this);
                    nameEditText.setText(newName);
                    nameEditText.setEnabled(false);
                }
            }
        }else if(v == profileimage){
            pickMediaLauncher.launch("image/*");
        }
    }

    // Method to set current user's name in EditText
    private void setUserNameInEditText() {
        if (currentUser != null && !currentUser.isAnonymous()) {
            nameEditText.setText(user.getUserName());
        } else {
            nameEditText.setText(R.string.anonymous);
        }
    }

    // Método para manejar el resultado de la selección de imagen
    private void handleImageSelectionResult(Uri selectedImageUri) {
        if (selectedImageUri != null) {
            profileimage.setImageURI(selectedImageUri);
        } else {
            // No se seleccionó ninguna imagen
            Log.d("ImagePicker", "No image selected");
        }
    }
}