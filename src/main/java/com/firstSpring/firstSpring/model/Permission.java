package com.firstSpring.firstSpring.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity to represent a user permission using the enum 'PermissionEnum' to define all user's permissions with some audit fields (deleted, createdAt)
 *
 * @author Daniel Mora Cantillo
 *
 * */
@Entity
@Table(name = "permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "permission_name", updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionsEnum permissionEnum;

    @JsonIgnore
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private LocalDate createdAt;

    @PrePersist
    protected void initAuditFields() {
        createdAt = LocalDate.now();
        deleted = false;
    }
}
