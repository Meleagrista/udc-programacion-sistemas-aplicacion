package com.cainzos.proyectofinal.recursos.objects;

public class User {
    // Variables de datos de usuario
    private String userId;
    private String userEmail;
    private String userName;
    private String tag;
    private String password;

    // Constructor
    public User(String userId, String userEmail, String userName, String tag, String password) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.tag = tag;
        this.password = password;
    }

    // Getters y setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
