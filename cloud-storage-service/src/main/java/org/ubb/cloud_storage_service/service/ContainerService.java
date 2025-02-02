package org.ubb.cloud_storage_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.ubb.cloud_storage_service.controller.api.ContainerRequest;
import org.ubb.cloud_storage_service.controller.api.ContainerResponse;
import org.ubb.cloud_storage_service.db.Container;
import org.ubb.cloud_storage_service.exception.BadRequestException;
import org.ubb.cloud_storage_service.exception.ObjectNotFoundException;
import org.ubb.cloud_storage_service.repository.ContainerRepository;
import org.ubb.cloud_storage_service.utils.Converter;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ContainerService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerService.class);

    private final ContainerRepository containerRepository;

    public ContainerService(ContainerRepository containerRepository)
    {
        this.containerRepository = containerRepository;
    }

    public ContainerResponse createContainer(ContainerRequest containerRequest)
    {
        LOGGER.info("Creating container, container request: {}", containerRequest);
        Container container = Converter.toContainer(containerRequest);
        ContainerResponse containerResponse = Converter.toContainerResponse(containerRepository.save(container));
        LOGGER.info("Done creating container, response: {}", containerResponse);
        return containerResponse;
    }

    public Optional<ContainerResponse> getContainer(String userName, UUID containerId)
    {
        LOGGER.info("Getting the container for user: {}, id: {}", userName, containerId);
        Optional<ContainerResponse> containerResponse = containerRepository.findByUserNameAndContainerId(userName, containerId)
                .map(Converter::toContainerResponse);
        LOGGER.info("Got the container, user: {}, id: {}, present: {}", userName, containerId, containerResponse.isPresent());
        return containerResponse;
    }

    public Page<ContainerResponse> getContainers(Pageable pageable)
    {
        LOGGER.info("Getting the containers for page: {}", pageable);
        Page<ContainerResponse> containers = containerRepository.findAllBy(pageable)
                .map(Converter::toContainerResponse);
        LOGGER.info("Got the containers for page: {}, items: {}", pageable, containers.getNumberOfElements());
        return containers;
    }

    public void deleteContainer(String userName, UUID containerId)
    {
        LOGGER.info("Will delete container, userName: {}, containerId: {}", userName, containerId);
        Container existingContainer = containerRepository.findByUserNameAndContainerId(userName, containerId)
                .orElseThrow(() -> new ObjectNotFoundException("Container not found"));
        if (!CollectionUtils.isEmpty(existingContainer.getObjectMetadataList()))
        {
            throw new BadRequestException("Cannot delete container, it is not empty");
        }
        containerRepository.delete(existingContainer);
        LOGGER.info("Deleted container, userName: {}, containerId: {}", userName, containerId);
    }
}
