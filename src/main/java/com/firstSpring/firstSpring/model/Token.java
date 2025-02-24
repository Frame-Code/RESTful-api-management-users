package com.firstSpring.firstSpring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Artist-Code
 */
@Entity(name = "tokens")
@Data
@Builder
public class Token implements Serializable{
    
    public enum TokenType {
        BEARER
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String token;
    
    private TokenType tokenType = TokenType.BEARER;
    
    public boolean revoked;
    
    public boolean expired;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
    
}
