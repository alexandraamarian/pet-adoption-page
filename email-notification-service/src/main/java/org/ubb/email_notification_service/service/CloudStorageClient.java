package org.ubb.email_notification_service.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.ubb.email_notification_service.config.CloudStorageProperties;
import org.ubb.email_notification_service.dto.ContainerResponse;

import java.util.UUID;

@Component
public class CloudStorageClient
{
    private final CloudStorageProperties cloudStorageProperties;
    private final RestTemplate restTemplate;

    public CloudStorageClient(CloudStorageProperties cloudStorageProperties, RestTemplateBuilder restTemplateBuilder)
    {
        this.cloudStorageProperties = cloudStorageProperties;
        this.restTemplate = restTemplateBuilder.build();
    }

    public ContainerResponse getContainer(String userName, UUID containerId)
    {
        String url = UriComponentsBuilder.fromUriString(cloudStorageProperties.getBaseUrl())
                .pathSegment("api", "containers", userName, containerId.toString())
                .toUriString();
        return restTemplate.getForObject(url, ContainerResponse.class);
    }

    public Resource getObjectContent(String userName, UUID objectId)
    {
        String url = UriComponentsBuilder.fromUriString(cloudStorageProperties.getBaseUrl())
                .pathSegment("api", "object-metadata", userName, objectId.toString(), "content")
                .toUriString();

        return restTemplate.getForObject(url, Resource.class, userName, objectId);
    }
}