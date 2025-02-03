package org.ubb.cloud_storage_service.controller.api;

import java.util.UUID;

public record ObjectMetadataResponse(UUID objectId, String userName, String objectName, String mimeType,
                                     String createdDate, UUID containerId)
{
}
