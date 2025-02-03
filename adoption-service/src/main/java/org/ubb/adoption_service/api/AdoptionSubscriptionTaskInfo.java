package org.ubb.adoption_service.api;

import java.util.UUID;

public record AdoptionSubscriptionTaskInfo(UUID taskId, UUID adoptionId, String userName, String posterEmail,
                                           String subscriberEmail)
{
}
