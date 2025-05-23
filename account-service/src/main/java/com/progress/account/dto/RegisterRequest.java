package com.progress.account.dto;

public class RegisterRequest {
    private String email;
    private String password_hash;
    private String first_name, last_name;
    private String role;

    public RegisterRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword_hash() { return password_hash; }
    public void setPassword_hash(String password_hash) { this.password_hash = password_hash; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
