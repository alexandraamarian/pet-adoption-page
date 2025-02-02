package org.ubb.cloud_storage_service.controller.api;

import java.util.List;
import java.util.UUID;

public record ContainerResponse(UUID containerId, String userName, List<ObjectMetadataResponse> content)
{
}
