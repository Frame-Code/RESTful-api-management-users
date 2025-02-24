package com.firstSpring.firstSpring.repository;

import com.firstSpring.firstSpring.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Artist-Code
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    
}
