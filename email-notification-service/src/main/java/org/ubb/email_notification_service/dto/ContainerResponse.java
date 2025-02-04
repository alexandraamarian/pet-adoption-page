package org.ubb.email_notification_service.dto;

import java.util.List;
import java.util.UUID;

public record ContainerResponse(UUID containerId, String userName, List<ObjectMetadataResponse> content)
{
}
