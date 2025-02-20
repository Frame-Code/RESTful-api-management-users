
package com.firstSpring.firstSpring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Artist-Code
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    @JsonProperty("acces_token")
    private String accesToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
}
