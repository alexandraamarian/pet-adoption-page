package org.ubb.email_notification_service.dto;

import java.util.UUID;

public record ObjectMetadataResponse(UUID objectId, String userName, String objectName, String mimeType,
                                     String createdDate, UUID containerId)
{
}
