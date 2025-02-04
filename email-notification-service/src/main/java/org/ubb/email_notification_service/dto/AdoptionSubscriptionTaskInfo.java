package org.ubb.email_notification_service.dto;

import java.util.UUID;

public record AdoptionSubscriptionTaskInfo(UUID taskId, UUID adoptionId, String userName, String posterEmail,
                                           String subscriberEmail)
{
}
