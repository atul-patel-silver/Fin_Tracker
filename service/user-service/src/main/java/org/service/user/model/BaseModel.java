package org.service.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(updatable = false,name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "update_at",insertable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by",updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "update_by",insertable = false)
    private String updateBy;
}

