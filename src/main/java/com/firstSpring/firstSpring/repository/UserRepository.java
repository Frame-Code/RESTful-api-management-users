package com.firstSpring.firstSpring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firstSpring.firstSpring.model.UserEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    @Query("SELECT u FROM UserEntity u WHERE u.deleted = false")
    List<UserEntity> findAllActiveUsers();
    
    @Query("SELECT u FROM UserEntity u WHERE u.id = :id AND u.deleted = false")
    Optional<UserEntity> findByIdActive(@Param("id") Long id);
    
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.deleted = false")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserEntity u WHERE u.phone = :phone AND u.deleted = false")
    Optional<UserEntity> findByNumberPhone(@Param("phone") String phone);

    @Query("SELECT u FROM UserEntity u WHere LOWER(u.name) LIKE :value OR LOWER(u.lastName) LIKE :value OR LOWER(u.email) = :value AND u.deleted = false")
    List<Optional<UserEntity>> findByNameOrEmail(@Param("value") String value);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.deleted = true WHERE u.id = :id")
    void softDeleteById(@Param("id")Long id);
}
