package org.ubb.adoption_service.api;

import java.util.UUID;

public record AdoptionSubscriptionResponse(UUID adoptionId, String posterUserName, String subscriberUserName,
                                           String status)
{
}
