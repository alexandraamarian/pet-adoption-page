package org.ubb.adoption_service.api.css;

import java.util.List;
import java.util.UUID;

public record ContainerResponse(UUID containerId, String userName, List<ObjectMetadataResponse> content)
{
}
