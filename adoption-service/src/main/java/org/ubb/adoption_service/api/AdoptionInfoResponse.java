package org.ubb.adoption_service.api;

import java.util.UUID;

public record AdoptionInfoResponse(UUID adoptionId, String userName, String petName, String petAge, String petType,
                                   String detailedInformation, String createdDate)
{
}

