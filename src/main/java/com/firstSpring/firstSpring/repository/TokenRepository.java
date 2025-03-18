package com.firstSpring.firstSpring.repository;

import com.firstSpring.firstSpring.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *  Repository to manage to tokens from the db
 *
 * @author Daniel Mora Cantillo
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long>{
    
}
