package com.progress.account.dto;

import com.progress.account.model.Role;

public class RegisterRequest {
    private String email;
    private String password;
    private String first_name, last_name;
    private Role role;

    public RegisterRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
