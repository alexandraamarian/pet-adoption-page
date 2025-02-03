package org.ubb.adoption_service.api;

import java.util.UUID;

public record AdoptionPreviewInfo(UUID adoptionId, String userName, String petName, String petAge, String petType,
                                  String createdDate, byte[] petImage)
{
}
