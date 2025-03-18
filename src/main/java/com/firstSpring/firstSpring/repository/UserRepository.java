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

/**
 * Repository to manage the users on the db, with 6 more methods
 * @author Daniel Mora Cantillo
 * */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Method to return a list of UserEntity where its attribute deleted as false
     * */
    @Query("SELECT u FROM UserEntity u WHERE u.deleted = false")
    List<UserEntity> findAllActiveUsers();

    /**
     * Method to return an Optional type UserEntity to get a User with an id
     * @param id the id to search
     * */
    @Query("SELECT u FROM UserEntity u WHERE u.id = :id AND u.deleted = false")
    Optional<UserEntity> findByIdActive(@Param("id") Long id);

    /**
     * Method to return an Optional type UserEntity to get a User with an email
     * @param email the email to search
     * */
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.deleted = false")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    /**
     * Method to return an Optional type UserEntity to get a User with an phone
     * @param phone the phone to search
     * */
    @Query("SELECT u FROM UserEntity u WHERE u.phone = :phone AND u.deleted = false")
    Optional<UserEntity> findByNumberPhone(@Param("phone") String phone);

    /**
     * Method to return a List type Optional type UserEntity to get a list of user when the attributes name, lastName or email like as the value
     * @param value the value to search
     * */
    @Query("SELECT u FROM UserEntity u WHERE (LOWER(u.name) LIKE :value OR LOWER(u.lastName) LIKE :value OR LOWER(u.email) LIKE :value) AND u.deleted = false")
    List<Optional<UserEntity>> findByNameOrEmail(@Param("value") String value);

    /**
     * Method to delete a user (changing its attribute deleted as true)
     * @param id the id to search
     * */
    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.deleted = true WHERE u.id = :id")
    void softDeleteById(@Param("id")Long id);
}
