package com.smartbank.dto;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;

    public UserResponse() {
    }

    public UserResponse(Long id,
                        String name,
                        String email,
                        String phone,
                        String role,
                        String status) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }
}