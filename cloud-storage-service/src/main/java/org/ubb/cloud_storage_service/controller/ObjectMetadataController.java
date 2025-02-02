package org.ubb.cloud_storage_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ubb.cloud_storage_service.controller.api.ObjectMetadataRequest;
import org.ubb.cloud_storage_service.controller.api.ObjectMetadataResponse;
import org.ubb.cloud_storage_service.exception.BadRequestException;
import org.ubb.cloud_storage_service.service.ObjectMetadataService;
import org.ubb.cloud_storage_service.utils.Converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/object-metadata") // More descriptive path
public class ObjectMetadataController
{

    private final ObjectMetadataService objectMetadataService; // Renamed service

    public ObjectMetadataController(ObjectMetadataService objectMetadataService)
    {
        this.objectMetadataService = objectMetadataService;
    }

    @PostMapping("/metadata")
    public ResponseEntity<ObjectMetadataResponse> createObjectMetadata(@RequestBody ObjectMetadataRequest objectMetadataRequest)
    {
        ObjectMetadataResponse response = objectMetadataService.createObjectMetadata(objectMetadataRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{userName}/{objectId}/content")
    public ResponseEntity<ObjectMetadataResponse> uploadObjectContent(@PathVariable String userName,
                                                                      @PathVariable UUID objectId,
                                                                      @RequestParam("file") MultipartFile file)
    {
        try (InputStream contentStream = file.getInputStream())
        {
            ObjectMetadataResponse response = objectMetadataService.uploadContent(userName, objectId, contentStream); // DTO
            return ResponseEntity.ok(response);
        } catch (IOException e)
        {
            throw new BadRequestException("Unable to open file data");
        }
    }

    @PostMapping
    public ResponseEntity<ObjectMetadataResponse> createAndUploadObject(
            @RequestParam("metadata") String metadata,
            @RequestParam("file") MultipartFile file)
    {
        try (InputStream contentStream = file.getInputStream())
        {
            ObjectMetadataRequest objectMetadataRequest = Converter.toObjectMetadataRequest(metadata);
            ObjectMetadataResponse response = objectMetadataService.createAndUploadObject(objectMetadataRequest, contentStream);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (JsonProcessingException e)
        {
            throw new BadRequestException("Unable to convert metadata to JSON");
        } catch (IOException e)
        {
            throw new BadRequestException("Unable to open file data");
        }
    }

    @GetMapping("/{userName}/{objectId}/metadata")
    public ResponseEntity<ObjectMetadataResponse> getObjectMetadata(
            @PathVariable String userName,
            @PathVariable UUID objectId)
    {
        return ResponseEntity.ok(objectMetadataService.getObjectMetadata(userName, objectId));
    }

    @GetMapping("/{userName}/{objectId}/content")
    public ResponseEntity<Resource> getObjectContent(
            @PathVariable String userName,
            @PathVariable UUID objectId,
            @RequestParam(value = "simple", defaultValue = "false") boolean isSimple)
    {

        ObjectMetadataResponse objectMetadata = objectMetadataService.getObjectMetadata(userName, objectId);
        String mimeType = Converter.toMimeType(objectMetadata.objectName());

        InputStream contentStream = objectMetadataService.getObjectContent(userName, objectId);
        Resource resource = new InputStreamResource(contentStream);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectMetadata.objectName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{userName}/{objectId}")
    public ResponseEntity<Void> deleteObject(@PathVariable String userName, @PathVariable UUID objectId)
    {
        objectMetadataService.deleteObject(userName, objectId);
        return ResponseEntity.noContent().build();
    }
}