package org.ubb.adoption_service.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.ubb.adoption_service.api.css.ContainerRequest;
import org.ubb.adoption_service.api.css.ContainerResponse;
import org.ubb.adoption_service.api.css.ObjectMetadataRequest;
import org.ubb.adoption_service.api.css.ObjectMetadataResponse;
import org.ubb.adoption_service.config.CloudStorageProperties;
import org.ubb.adoption_service.utils.CustomPageImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public ContainerResponse createContainer(ContainerRequest request)
    {
        String url = cloudStorageProperties.getBaseUrl() + "/api/containers";
        return restTemplate.postForObject(url, request, ContainerResponse.class);
    }

    public ContainerResponse getContainer(String userName, UUID containerId)
    {
        String url = UriComponentsBuilder.fromUriString(cloudStorageProperties.getBaseUrl())
                .pathSegment("api", "containers", userName, containerId.toString())
                .toUriString();
        return restTemplate.getForObject(url, ContainerResponse.class);
    }

    public Page<ContainerResponse> getAllContainers(Pageable pageable)
    {
        String url = UriComponentsBuilder.fromUriString(cloudStorageProperties.getBaseUrl())
                .pathSegment("api", "containers")
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .toUriString();

        ResponseEntity<CustomPageImpl<ContainerResponse>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<CustomPageImpl<ContainerResponse>>()
                {
                });
        return response.getBody();
    }

    public void deleteContainer(String userName, UUID containerId)
    {
        String url = UriComponentsBuilder.fromUriString(cloudStorageProperties.getBaseUrl())
                .pathSegment("api", "containers", userName, containerId.toString())
                .toUriString();
        restTemplate.delete(url);
    }

    public ObjectMetadataResponse createObjectMetadata(ObjectMetadataRequest request)
    {
        String url = cloudStorageProperties.getBaseUrl() + "/api/object-metadata/metadata";
        return restTemplate.postForObject(url, request, ObjectMetadataResponse.class);
    }

    public ObjectMetadataResponse uploadObjectContent(String userName, UUID objectId, MultipartFile file)
    {
        String url = UriComponentsBuilder.fromUriString(cloudStorageProperties.getBaseUrl())
                .pathSegment("api", "object-metadata", userName, objectId.toString(), "content")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try
        {
            body.add("file", new ByteArrayResource(file.getBytes())
            {
                @Override
                public String getFilename()
                {
                    return file.getOriginalFilename();
                }
            });
        } catch (IOException e)
        {
            throw new RuntimeException("Unable to upload file content", e); // Or a custom exception
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, requestEntity, ObjectMetadataResponse.class, userName, objectId);
    }

    public void createAndUploadObject(String metadata, String fileContent, String filename)
    {
        String url = cloudStorageProperties.getBaseUrl() + "/api/object-metadata";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("metadata", metadata);
        body.add("file", new ByteArrayResource(fileContent.getBytes(StandardCharsets.UTF_8))
        {
            @Override
            public String getFilename()
            {
                return filename;
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        restTemplate.postForObject(url, requestEntity, ObjectMetadataResponse.class);
    }

    public void createAndUploadObject(String metadata, MultipartFile file)
    {
        String url = cloudStorageProperties.getBaseUrl() + "/api/object-metadata";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("metadata", metadata); // No need for JSON conversion here, it's already a string
        try
        {
            body.add("file", new ByteArrayResource(file.getBytes())
            {
                @Override
                public String getFilename()
                {
                    return file.getName();
                }
            });
        } catch (IOException e)
        {
            throw new RuntimeException("Unable to upload file content", e);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        restTemplate.postForObject(url, requestEntity, ObjectMetadataResponse.class);
    }

    public ObjectMetadataResponse getObjectMetadata(String userName, UUID objectId)
    {
        String url = UriComponentsBuilder.fromUriString(cloudStorageProperties.getBaseUrl())
                .pathSegment("api", "object-metadata", userName, objectId.toString(), "metadata")
                .toUriString();
        return restTemplate.getForObject(url, ObjectMetadataResponse.class, userName, objectId);
    }

    public Resource getObjectContent(String userName, UUID objectId)
    {
        String url = UriComponentsBuilder.fromUriString(cloudStorageProperties.getBaseUrl())
                .pathSegment("api", "object-metadata", userName, objectId.toString(), "content")
                .toUriString();

        return restTemplate.getForObject(url, Resource.class, userName, objectId);
    }

    public void deleteObject(String userName, UUID objectId)
    {
        String url = UriComponentsBuilder.fromUriString(cloudStorageProperties.getBaseUrl())
                .pathSegment("api", "object-metadata", userName, objectId.toString())
                .toUriString();

        restTemplate.delete(url, userName, objectId);
    }
}
