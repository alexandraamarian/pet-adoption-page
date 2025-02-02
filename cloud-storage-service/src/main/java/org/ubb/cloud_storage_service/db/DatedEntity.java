package org.ubb.cloud_storage_service.db;


import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@MappedSuperclass
public class DatedEntity
{
    @CreationTimestamp
    private Instant createdTime;

    @UpdateTimestamp
    private Instant updatedTime;

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getUpdatedTime()
    {
        return updatedTime;
    }
}
