package com.firstSpring.firstSpring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditUser {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private Set<String> roles;

}
