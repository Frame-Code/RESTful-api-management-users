
package com.firstSpring.firstSpring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Artist-Code
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {
    private String email;
    private String password;
}
