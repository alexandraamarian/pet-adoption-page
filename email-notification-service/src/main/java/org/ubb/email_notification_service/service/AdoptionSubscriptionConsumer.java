package org.ubb.email_notification_service.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.ubb.email_notification_service.dto.AdoptionSubscriptionTaskInfo;
import org.ubb.email_notification_service.utils.Converter;

@Component
public class AdoptionSubscriptionConsumer
{
    private final TaskProcessorService taskProcessorService;

    public AdoptionSubscriptionConsumer(TaskProcessorService taskProcessorService)
    {
        this.taskProcessorService = taskProcessorService;
    }

    @RabbitListener(queues = "adoptionSubscriptionQueue")
    public void receiveMessage(String message)
    {
        // Convert JSON message to object
        AdoptionSubscriptionTaskInfo taskInfo = Converter.toAdoptionSubscriptionTaskInfo(message);

        // Process the task
        taskProcessorService.processTask(taskInfo);
    }
}