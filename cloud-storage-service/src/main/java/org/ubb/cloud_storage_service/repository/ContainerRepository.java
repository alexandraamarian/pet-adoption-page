package org.ubb.cloud_storage_service.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ubb.cloud_storage_service.db.Container;

import java.util.Optional;
import java.util.UUID;

public interface ContainerRepository extends JpaRepository<Container, UUID>
{
    Optional<Container> findByUserNameAndContainerId(String userName, UUID containerId);

    Page<Container> findAllBy(Pageable pageable);
}
