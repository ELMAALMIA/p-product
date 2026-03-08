package com.eTrust.product.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class BaseAuditableEntity {
    @CreatedDate
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Long createdAt;
    @LastModifiedDate
    @Column(
            name = "updated_at"
    )
    private Long updatedAt;

    public Long getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Long createdDate) {
        this.createdAt = createdDate;
    }

    public Long getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Long lastModifiedDate) {
        this.updatedAt = lastModifiedDate;
    }
}