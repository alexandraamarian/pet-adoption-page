package org.ubb.cloud_storage_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ubb.cloud_storage_service.db.ObjectMetadata;

import java.util.Optional;
import java.util.UUID;

public interface ObjectMetadataRepository extends JpaRepository<ObjectMetadata, UUID>
{
    Optional<ObjectMetadata> findByObjectIdAndUserName(UUID objectId, String userName);
}
