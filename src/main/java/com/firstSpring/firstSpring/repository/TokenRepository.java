package com.firstSpring.firstSpring.repository;

import com.firstSpring.firstSpring.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Artist-Code
 */
public interface TokenRepository extends JpaRepository<Token, Long>{
    
}
