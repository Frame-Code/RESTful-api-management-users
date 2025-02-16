package com.firstSpring.firstSpring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Artist-Code
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class User {

    private Long id;
    
    @NonNull
    private String name;
    @NonNull
    private String lastName;
    @NonNull
    private String email;
    @NonNull
    private String phone;
    @NonNull
    private String password;

}
