package com.firstSpring.firstSpring.repository;

import com.firstSpring.firstSpring.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  Repository to manage roles on the db, with 2 more methods.
 *
 * @author Daniel Mora Cantillo
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

    /** Method to get a List of all roles saved in the db with its attribute deleted as false
     * */
    @Query("SELECT r FROM Role r WHERE r.deleted = false")
    List<Role> findAllActive();

    /** Method to get a List of all roles saved on the db that look like a list type string with names
     * Is used when the list type string "names"
     * may be having names with any other thing to the name roles saved on the db
     * @param names the list type String with the names to search
     * */
    @Query(value = "SELECT * FROM roles WHERE is_deleted = false AND role_name IN :names", nativeQuery = true)
    List<Role> findByNames(@Param("names") List<String> names);
    
}
