package com.firstSpring.firstSpring.repository;

import com.firstSpring.firstSpring.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Artist-Code
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

    @Query("SELECT r FROM Role r WHERE r.deleted = false")
    List<Role> findAllActive();

    @Query(value = "SELECT * FROM roles WHERE is_deleted = false AND role_name IN :names", nativeQuery = true)
    List<Role> findByNames(@Param("names") List<String> names);
    
}
