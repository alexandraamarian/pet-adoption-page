package org.ubb.adoption_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.ubb.adoption_service.api.*;
import org.ubb.adoption_service.api.css.ContainerRequest;
import org.ubb.adoption_service.api.css.ContainerResponse;
import org.ubb.adoption_service.api.css.ObjectMetadataRequest;
import org.ubb.adoption_service.api.css.ObjectMetadataResponse;
import org.ubb.adoption_service.exception.DataProcessingException;
import org.ubb.adoption_service.exception.ObjectNotFoundException;
import org.ubb.adoption_service.model.UserEntity;
import org.ubb.adoption_service.repository.UserRepository;
import org.ubb.adoption_service.utils.Converter;

import java.io.IOException;
import java.util.UUID;

@Service
public class AdoptionService
{
    private static final String ADOPTION_INFO_FILENAME = "adoption_info.json";

    private final CloudStorageClient cloudStorageClient;
    private final RabbitMqProducer rabbitMqProducer;
    private final UserRepository userRepository;

    public AdoptionService(CloudStorageClient cloudStorageClient, RabbitMqProducer rabbitMqProducer, UserRepository userRepository)
    {
        this.cloudStorageClient = cloudStorageClient;
        this.rabbitMqProducer = rabbitMqProducer;
        this.userRepository = userRepository;
    }

    public AdoptionInfoResponse createAdoptionPost(String userName, String petName, String petAge, String petType, String detailedInformation, MultipartFile image)
    {
        // Create a container for the post
        ContainerRequest containerRequest = new ContainerRequest(userName);
        ContainerResponse containerResponse = cloudStorageClient.createContainer(containerRequest);

        // Post the image and detailed content in the container
        ObjectMetadataRequest imageMetadataRequest = new ObjectMetadataRequest(
                userName, image.getName(), image.getContentType(), containerResponse.containerId()
        );
        String imageMetadata;
        try
        {
            imageMetadata = Converter.toJsonString(imageMetadataRequest);
        } catch (JsonProcessingException e)
        {
            throw new DataProcessingException("Unable to create metadata for image", e);
        }
        cloudStorageClient.createAndUploadObject(imageMetadata, image);

        ObjectMetadataRequest adoptionInformationMetadataRequest = new ObjectMetadataRequest(
                userName, ADOPTION_INFO_FILENAME, "application/json", containerResponse.containerId()
        );
        AdoptionContent adoptionContent = new AdoptionContent(
                petName, petAge, petType, detailedInformation
        );
        String adoptionInformationMetadata;
        String adoptionContentJson;
        try
        {
            adoptionInformationMetadata = Converter.toJsonString(adoptionInformationMetadataRequest);
            adoptionContentJson = Converter.toJsonString(adoptionContent);
        } catch (JsonProcessingException e)
        {
            throw new DataProcessingException("Unable to create metadata for image", e);
        }
        cloudStorageClient.createAndUploadObject(adoptionInformationMetadata, adoptionContentJson, ADOPTION_INFO_FILENAME);

        AdoptionInfoResponse adoptionInfoResponse = new AdoptionInfoResponse(containerResponse.containerId(),
                userName, petName, petAge, petType, detailedInformation, Converter.toCurrentTime());

        return adoptionInfoResponse;
    }

    public Page<AdoptionPreviewInfo> getAdoptionsPreviews(Pageable pageable)
    {
        // Get containers from CSS that contain adoptions data
        Page<ContainerResponse> containers = cloudStorageClient.getAllContainers(pageable);

        Page<AdoptionPreviewInfo> adoptions = containers
                .map(this::getContainerAdoptionPreview);

        return adoptions;
    }

    public AdoptionSubscriptionResponse subscribeToAdoption(AdoptionSubscriptionRequest adoptionSubscription)
    {
        UserEntity adoptionCreator = userRepository.findByUsername(adoptionSubscription.posterUserName())
                .orElseThrow(() -> new ObjectNotFoundException("Unable to find data for user: " + adoptionSubscription.posterUserName()));
        UserEntity adoptionSubscriber = userRepository.findByUsername(adoptionSubscription.subscriberUserName())
                .orElseThrow(() -> new ObjectNotFoundException("Unable to find data for user: " + adoptionSubscription.subscriberUserName()));
        AdoptionSubscriptionTaskInfo adoptionSubscriptionTaskInfo = new AdoptionSubscriptionTaskInfo(
                UUID.randomUUID(),
                adoptionSubscription.adoptionId(),
                adoptionSubscription.posterUserName(),
                adoptionCreator.getEmail(),
                adoptionSubscriber.getEmail()
        );
        rabbitMqProducer.sendTask(adoptionSubscriptionTaskInfo);
        AdoptionSubscriptionResponse adoptionSubscriptionResponse = new AdoptionSubscriptionResponse(
                adoptionSubscription.adoptionId(),
                adoptionSubscription.posterUserName(),
                adoptionSubscription.subscriberUserName(),
                "COMPLETED"
        );
        return adoptionSubscriptionResponse;
    }

    public void deleteAdoptionPost(String userName, UUID adoptionId)
    {
        ContainerResponse containerResponse = cloudStorageClient.getContainer(userName, adoptionId);
        if (!CollectionUtils.isEmpty(containerResponse.content()))
        {
            containerResponse.content()
                    .forEach(objectMetadata -> cloudStorageClient.deleteObject(userName, objectMetadata.objectId()));
        }
        cloudStorageClient.deleteContainer(userName, adoptionId);
    }

    private AdoptionPreviewInfo getContainerAdoptionPreview(ContainerResponse container)
    {
        ObjectMetadataResponse adoptionInfo = container.content().stream()
                .filter(objectMetadata -> ADOPTION_INFO_FILENAME.equals(objectMetadata.objectName()))
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException("Unable to find information"));
        Resource adoptionData = cloudStorageClient.getObjectContent(adoptionInfo.userName(), adoptionInfo.objectId());
        AdoptionContent adoptionContent = Converter.toAdoptionContent(adoptionData);
        ObjectMetadataResponse imageInfo = container.content().stream()
                .filter(objectMetadata -> !ADOPTION_INFO_FILENAME.equals(objectMetadata.objectName()))
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException("Unable to find image data"));
        Resource imageData = cloudStorageClient.getObjectContent(imageInfo.userName(), imageInfo.objectId());
        try
        {
            return new AdoptionPreviewInfo(
                    container.containerId(),
                    adoptionInfo.userName(),
                    adoptionContent.petName(),
                    adoptionContent.petAge(),
                    adoptionContent.petType(),
                    adoptionInfo.createdDate(),
                    imageData.getContentAsByteArray()
            );
        } catch (IOException e)
        {
            throw new DataProcessingException("Unable to get image data", e);
        }
    }
}
