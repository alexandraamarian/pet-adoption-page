package org.ubb.adoption_service.api.css;

import java.util.UUID;

public record ObjectMetadataResponse(UUID objectId, String userName, String objectName, String mimeType,
                                     String createdDate, UUID containerId)
{
}
