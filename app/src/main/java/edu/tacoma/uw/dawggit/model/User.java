package edu.tacoma.uw.dawggit.model;

public class User {

    private String mUsername;
    private String mEmail;
    private String mPassword;

    public User(String username, String email, String password) {
        this.mUsername = username;
        this.mEmail = email;
        this.mPassword = password;
    }

    public String getUsername() {
        return this.mUsername;
    }

    public String getEmail() {
        return this.mEmail;
    }

    public String getPassword() {
        return this.mPassword;
    }
}
