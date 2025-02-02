package org.ubb.cloud_storage_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ubb.cloud_storage_service.controller.api.ContainerRequest;
import org.ubb.cloud_storage_service.controller.api.ContainerResponse;
import org.ubb.cloud_storage_service.exception.ObjectNotFoundException;
import org.ubb.cloud_storage_service.service.ContainerService;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/containers")
public class ContainerController
{
    private final ContainerService containerService;

    public ContainerController(ContainerService containerService)
    {
        this.containerService = containerService;
    }

    @PostMapping
    public ResponseEntity<ContainerResponse> createContainer(@RequestBody ContainerRequest containerRequest)
    {
        ContainerResponse response = containerService.createContainer(containerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userName}/{containerId}")
    public ResponseEntity<ContainerResponse> getContainer(
            @PathVariable String userName,
            @PathVariable UUID containerId)
    {
        return containerService.getContainer(userName, containerId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ObjectNotFoundException("Container not found"));
    }

    @GetMapping
    public ResponseEntity<Page<ContainerResponse>> getAllContainers(
            @PageableDefault(size = 5, sort = "createdTime", direction = Sort.Direction.DESC) Pageable pageable)
    {
        Page<ContainerResponse> containers = containerService.getContainers(pageable);
        return ResponseEntity.ok(containers);
    }
    
    @DeleteMapping("/{userName}/{containerId}")
    public ResponseEntity<Void> deleteContainer(
            @PathVariable String userName,
            @PathVariable UUID containerId)
    {

        containerService.deleteContainer(userName, containerId);
        return ResponseEntity.noContent().build();
    }
}