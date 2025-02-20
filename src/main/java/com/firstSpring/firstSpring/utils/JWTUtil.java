
package com.firstSpring.firstSpring.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Artist-Code
 */
@Component
public class JWTUtil {
    
    private String key;
    
    private String issuer;
    
    private Long ttlMillis;
    
}
