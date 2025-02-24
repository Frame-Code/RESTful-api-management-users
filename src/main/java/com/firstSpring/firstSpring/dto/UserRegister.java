package com.firstSpring.firstSpring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *s
 * @author Artist-Code
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegister {
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String password;
}
