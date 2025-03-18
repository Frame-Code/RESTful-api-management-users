package com.firstSpring.firstSpring.dto;

import com.firstSpring.firstSpring.model.Token;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Daniel Mora Cantillo
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private List<Token> tokens;
}
