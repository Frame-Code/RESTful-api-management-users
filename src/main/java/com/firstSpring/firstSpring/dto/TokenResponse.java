
package com.firstSpring.firstSpring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Daniel Mora Cantillo
 *
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    @JsonProperty("access_token")
    private String access_token;
    
    @JsonProperty("refresh_token")
    private String refresh_token;

    @JsonProperty("user_name")
    private String user_name;
}
