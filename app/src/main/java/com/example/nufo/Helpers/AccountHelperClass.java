package com.example.nufo.Helpers;

public class AccountHelperClass {


    String email, username;

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

    public AccountHelperClass(String username, String email) {
        this.username = username;
        this.email = email;
    }



    public AccountHelperClass() {
    }

}
