package org.ubb.adoption_service.api.css;

import java.util.UUID;

public record ObjectMetadataRequest(String userName, String objectName, String mimeType, UUID containerId)
{
}
