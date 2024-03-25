package com.cainzos.proyectofinal.recursos;

import com.cainzos.proyectofinal.recursos.User;

public class FriendRequest {
    private final User user;
    private String status;


    // Constructor
    public FriendRequest(User user, String status) {
        this.user = user;
        this.status = status;
    }

    // Getters y setters

    public User getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
