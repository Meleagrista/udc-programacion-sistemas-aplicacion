package com.cainzos.proyectofinal.fragments_menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cainzos.proyectofinal.LoginActivity;
import com.cainzos.proyectofinal.R;
import com.cainzos.proyectofinal.recursos.objects.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoomFragment extends Fragment {

    private static final String INITIAL_GAME_DATA = "Datos iniciales del juego";

    private DatabaseReference roomsRef;
    private String roomId;
    private EditText roomIdEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        roomIdEditText = view.findViewById(R.id.room_id_edittext);
        // Inicializar la referencia a la base de datos de Firebase
        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");

        Button createRoomButton = view.findViewById(R.id.create_room_button);
        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRoom();
            }
        });

        Button joinRoomButton = view.findViewById(R.id.join_room_button);
        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinRoom();
            }
        });

        return view;
    }

    // Método para crear una nueva sala
    private void createRoom() {
        String roomId = roomsRef.push().getKey();
        Room room = new Room(roomId, INITIAL_GAME_DATA); // Define initialGameData según tus necesidades
        assert roomId != null;
        roomsRef.child(roomId).setValue(room);
        this.roomId = roomId;
    }

    // Método para unirse a una sala existente
    private void joinRoom() {
        String roomId = roomIdEditText.getText().toString().trim();

        // Verificar si el campo de ID de la sala no está vacío
        if (!roomId.isEmpty()) {
            roomsRef.child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Room room = snapshot.getValue(Room.class);
                    if (room != null) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.putExtra("roomId", roomId);
                        startActivity(intent);
                    } else {
                        // La sala no existe o ya está llena
                        // Aquí podrías mostrar un mensaje de error al usuario
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Manejar errores
                    // Por ejemplo, mostrar un mensaje de error al usuario
                }
            });
        } else {
            // El campo de ID de la sala está vacío
            // Aquí podrías mostrar un mensaje de error al usuario
        }
    }


}
