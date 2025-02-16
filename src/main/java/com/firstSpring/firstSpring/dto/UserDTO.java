package com.firstSpring.firstSpring.dto;

/**
 *
 * @author Artist-Code
 */
public class UserDTO {
    private String name;
    private String lastName;
    private String email;
    private String phone;

    public UserDTO() {
    }
    
    public UserDTO(String name, String lastName, String email, String phone) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "UserDTO{" + "name=" + name + ", lastName=" + lastName + ", email=" + email + ", phone=" + phone + '}';
    }
    
}
