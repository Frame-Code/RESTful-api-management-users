package com.firstSpring.firstSpring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firstSpring.firstSpring.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.deleted = false")
    List<User> findAllActiveUsers();
    
    @Query("Select u FROM User u WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdActive(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.deleted = true WHERE u.id = :id")
    void softDeleteById(@Param("id")Long id);
}
