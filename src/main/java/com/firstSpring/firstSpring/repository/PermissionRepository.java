package com.firstSpring.firstSpring.repository;

import com.firstSpring.firstSpring.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Artist-Code
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>{
    
}
