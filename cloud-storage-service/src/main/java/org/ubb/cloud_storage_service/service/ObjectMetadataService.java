package org.ubb.cloud_storage_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ubb.cloud_storage_service.controller.api.ObjectMetadataRequest;
import org.ubb.cloud_storage_service.controller.api.ObjectMetadataResponse;
import org.ubb.cloud_storage_service.db.Container;
import org.ubb.cloud_storage_service.db.ObjectMetadata;
import org.ubb.cloud_storage_service.exception.BadRequestException;
import org.ubb.cloud_storage_service.exception.ObjectNotFoundException;
import org.ubb.cloud_storage_service.repository.ContainerRepository;
import org.ubb.cloud_storage_service.repository.ObjectMetadataRepository;
import org.ubb.cloud_storage_service.utils.Converter;

import java.io.InputStream;
import java.util.UUID;

@Service
@Transactional
public class ObjectMetadataService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMetadataService.class);

    private final ObjectMetadataRepository objectMetadataRepository;
    private final ContainerRepository containerRepository;
    private final AzureBlobStorageClient azureBlobStorageClient;

    public ObjectMetadataService(ObjectMetadataRepository objectMetadataRepository, ContainerRepository containerRepository, AzureBlobStorageClient azureBlobStorageClient)
    {
        this.objectMetadataRepository = objectMetadataRepository;
        this.containerRepository = containerRepository;
        this.azureBlobStorageClient = azureBlobStorageClient;
    }

    public ObjectMetadataResponse createObjectMetadata(ObjectMetadataRequest objectMetadataRequest)
    {
        LOGGER.info("Creating object metadata, request: {}", objectMetadataRequest);
        // Check the container id
        if (objectMetadataRequest == null || objectMetadataRequest.containerId() == null)
        {
            throw new BadRequestException("Container id is required");
        }
        Container container = containerRepository.findById(objectMetadataRequest.containerId())
                .orElseThrow(() -> new ObjectNotFoundException("Cannot find container with id: " + objectMetadataRequest.containerId()));
        ObjectMetadata objectMetadata = Converter.toObjectMetadata(objectMetadataRequest, container);
        ObjectMetadataResponse response = Converter.toObjectMetadataResponse(objectMetadataRepository.save(objectMetadata));
        LOGGER.info("Created object metadata, response: {}", response);
        return response;
    }

    public ObjectMetadataResponse uploadContent(String userName, UUID objectId, InputStream content)
    {
        LOGGER.info("Uploading object content, userName: {}, objectId: {}", userName, objectId);
        ObjectMetadata objectMetadata = getObjectOrThrow(userName, objectId);
        azureBlobStorageClient.saveFile(userName, objectMetadata.getContainer().getContainerId(), objectMetadata.getObjectName(), content);
        ObjectMetadataResponse response = Converter.toObjectMetadataResponse(objectMetadata);
        LOGGER.info("Uploaded object content, userName: {}, objectId: {}", userName, objectId);
        return response;
    }

    public ObjectMetadataResponse createAndUploadObject(ObjectMetadataRequest objectMetadataRequest, InputStream content)
    {
        LOGGER.info("Creating object and uploading metadata and content, request: {}", objectMetadataRequest);
        var metadataResponse = createObjectMetadata(objectMetadataRequest);
        var uploadResponse = uploadContent(metadataResponse.userName(), metadataResponse.objectId(), content);
        LOGGER.info("Created object and uploading metadata, response: {}", uploadResponse);
        return uploadResponse;
    }

    public ObjectMetadataResponse getObjectMetadata(String userName, UUID objectId)
    {
        LOGGER.info("Getting object metadata, userName: {}, objectId: {}", userName, objectId);
        return Converter.toObjectMetadataResponse(getObjectOrThrow(userName, objectId));
    }

    public InputStream getObjectContent(String userName, UUID objectId)
    {
        LOGGER.info("Getting object content, userName: {}, objectId: {}", userName, objectId);
        ObjectMetadata objectMetadata = getObjectOrThrow(userName, objectId);
        InputStream content = azureBlobStorageClient.retrieveFile(userName, objectMetadata.getContainer().getContainerId(), objectMetadata.getObjectName());
        LOGGER.info("Done getting object content, userName: {}, objectId: {}", userName, objectId);
        return content;
    }

    public void deleteObject(String userName, UUID objectId)
    {
        LOGGER.info("Deleting object, userName: {}, objectId: {}", userName, objectId);
        ObjectMetadata objectMetadata = getObjectOrThrow(userName, objectId);
        azureBlobStorageClient.removeFile(userName, objectMetadata.getContainer().getContainerId(), objectMetadata.getObjectName());
        objectMetadataRepository.deleteById(objectId);
        LOGGER.info("Deleted object, userName: {}, objectId: {}", userName, objectId);
    }

    private ObjectMetadata getObjectOrThrow(String userName, UUID objectId)
    {
        return objectMetadataRepository.findByObjectIdAndUserName(objectId, userName)
                .orElseThrow(() -> new ObjectNotFoundException("Object not found for userName: " + userName + " and objectId: " + objectId));
    }
}
