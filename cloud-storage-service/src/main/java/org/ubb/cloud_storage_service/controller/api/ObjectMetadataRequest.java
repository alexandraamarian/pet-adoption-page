package org.ubb.cloud_storage_service.controller.api;

import java.util.UUID;

public record ObjectMetadataRequest(String userName, String objectName, String mimeType, UUID containerId)
{
}
