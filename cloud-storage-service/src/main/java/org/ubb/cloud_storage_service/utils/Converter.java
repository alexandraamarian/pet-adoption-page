package org.ubb.cloud_storage_service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ubb.cloud_storage_service.controller.api.ContainerRequest;
import org.ubb.cloud_storage_service.controller.api.ContainerResponse;
import org.ubb.cloud_storage_service.controller.api.ObjectMetadataRequest;
import org.ubb.cloud_storage_service.controller.api.ObjectMetadataResponse;
import org.ubb.cloud_storage_service.db.Container;
import org.ubb.cloud_storage_service.db.ObjectMetadata;

import java.util.List;
import java.util.stream.Collectors;

public class Converter
{
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Container toContainer(ContainerRequest request)
    {
        Container container = new Container();
        container.setUserName(request.userName());
        container.setObjectMetadataList(List.of()); // Initialize an empty list
        return container;
    }

    public static ContainerResponse toContainerResponse(Container container)
    {
        List<ObjectMetadataResponse> objectMetadataResponses = container.getObjectMetadataList().stream()
                .map(Converter::toObjectMetadataResponse)
                .collect(Collectors.toList());

        return new ContainerResponse(
                container.getContainerId(),
                container.getUserName(),
                objectMetadataResponses
        );
    }

    public static ObjectMetadata toObjectMetadata(ObjectMetadataRequest request)
    {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setUserName(request.userName());
        objectMetadata.setObjectName(request.objectName());
        objectMetadata.setMimeType(request.mimeType());
        return objectMetadata;
    }

    public static ObjectMetadata toObjectMetadata(ObjectMetadataRequest request, Container container)
    {
        ObjectMetadata objectMetadata = toObjectMetadata(request);
        objectMetadata.setContainer(container);
        return objectMetadata;
    }

    public static ObjectMetadataResponse toObjectMetadataResponse(ObjectMetadata objectMetadata)
    {
        return new ObjectMetadataResponse(
                objectMetadata.getObjectId(),
                objectMetadata.getUserName(),
                objectMetadata.getObjectName(),
                objectMetadata.getMimeType(),
                objectMetadata.getCreatedTime() == null ? "" : objectMetadata.getCreatedTime().toString(),
                objectMetadata.getContainer().getContainerId()
        );
    }

    public static ObjectMetadataRequest toObjectMetadataRequest(String jsonData) throws JsonProcessingException
    {
        return Converter.mapper.readValue(jsonData, ObjectMetadataRequest.class);
    }

    public static String toMimeType(String fileName)
    {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return switch (extension.toLowerCase())
        {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "txt" -> "text/plain";
            default -> "application/octet-stream";
        };
    }
}
