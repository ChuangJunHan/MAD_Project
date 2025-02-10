package com.sp.mad_project;

public class HelperClass {

    String Username, email,phoneNumber,password;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HelperClass(String Username, String email, String phoneNumber, String password) {
        this.Username = Username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public HelperClass() {

    }
}
