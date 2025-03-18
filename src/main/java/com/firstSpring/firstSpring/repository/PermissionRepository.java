package com.firstSpring.firstSpring.repository;

import com.firstSpring.firstSpring.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *  Repository to manage the permissions on the db, but is not used for now because when a role is saved
 *  use CascadingType type All to save a set of permissions automatically
 *
 * @author Daniel Mora Cantillo
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>{
    
}
