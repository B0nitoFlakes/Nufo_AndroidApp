package com.example.nufo.Helpers;

public class AccountHelperClass {

    public String email, username;

    public AccountHelperClass(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }





    public AccountHelperClass() {
    }

}
