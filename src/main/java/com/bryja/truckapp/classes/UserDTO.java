package com.bryja.truckapp.classes;


public class UserDTO {
    public Long id;
    public String name;
    public String email;
    public boolean approved;


    public UserDTO() {
    }

    public UserDTO(Long id, String name, String email, boolean approved) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.approved = approved;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
