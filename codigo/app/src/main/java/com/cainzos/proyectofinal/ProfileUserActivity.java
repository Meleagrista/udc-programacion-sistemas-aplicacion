package com.cainzos.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileUserActivity extends AppCompatActivity implements View.OnClickListener {

    EditText nameEditText, extraEditText;

    Button editExtraFieldButton, editUsernameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        nameEditText = findViewById(R.id.UserNameEditText);
        extraEditText = findViewById(R.id.UserExtraFieldEditText);

        editUsernameButton = findViewById(R.id.editUserName);
        editExtraFieldButton = findViewById(R.id.editExtraField);
        editUsernameButton.setOnClickListener(this);
        editExtraFieldButton.setOnClickListener(this);

        extraEditText.setEnabled(false);
        nameEditText.setEnabled(false);

        extraEditText.setText(R.string.defaultTeam);
        nameEditText.setText(R.string.defaultUserName);
    }

    @Override
    public void onClick(View v) {
        if(v == editExtraFieldButton){
            if(!extraEditText.isEnabled()) {
                extraEditText.setEnabled(true);
                Toast.makeText(this, "Editing username is now allowed", Toast.LENGTH_SHORT).show();
            } else {
                extraEditText.setEnabled(false);
            }
        } else if (v == editUsernameButton){
            if(!nameEditText.isEnabled()) {
                nameEditText.setEnabled(true);
                Toast.makeText(this, "Editing extra is now allowed", Toast.LENGTH_SHORT).show();
            } else {
                nameEditText.setEnabled(false);
            }
        }
    }
}