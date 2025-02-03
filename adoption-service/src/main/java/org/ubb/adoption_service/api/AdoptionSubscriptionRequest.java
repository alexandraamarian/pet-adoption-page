package org.ubb.adoption_service.api;

import java.util.UUID;

public record AdoptionSubscriptionRequest(UUID adoptionId, String posterUserName, String subscriberUserName)
{
}
