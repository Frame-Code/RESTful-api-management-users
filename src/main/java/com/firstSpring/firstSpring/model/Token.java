package com.firstSpring.firstSpring.model;

import jakarta.persistence.*;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Artist-Code
 */
@Entity(name = "tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(unique = true, columnDefinition = "TEXT")
    private String token;
    
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    
    public boolean revoked;
    
    public boolean expired;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    public UserEntity user;
    
}
