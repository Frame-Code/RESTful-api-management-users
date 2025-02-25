package com.firstSpring.firstSpring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Artist-Code
 */
@Entity
@Table(name = "permissions")
@Data
@Builder
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "permission_name", updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionsEnum permissionEnum;
}
