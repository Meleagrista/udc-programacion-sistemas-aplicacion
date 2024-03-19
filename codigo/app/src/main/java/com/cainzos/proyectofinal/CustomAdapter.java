package com.cainzos.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private static final int[] items = {R.drawable.previa, R.drawable.proximamente, R.drawable.proximamente};


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_button, parent, false);
        // Aquí ajustamos el tamaño del diseño del elemento
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton imageButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.imageButton);
        }

        void bind(int imageResId) {
            imageButton.setImageResource(imageResId);
            imageButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (items[position] == R.drawable.previa){
                    Intent intent = new Intent(v.getContext(), GameMode1Activity.class);
                    v.getContext().startActivity(intent);
                } else if(items[position] == R.drawable.proximamente){
                    Toast.makeText(v.getContext(), "PROXIMAMENTE...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
