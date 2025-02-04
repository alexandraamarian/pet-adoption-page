package org.ubb.email_notification_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.ubb.email_notification_service.dto.AdoptionSubscriptionTaskInfo;
import org.ubb.email_notification_service.utils.Converter;

@Component
public class AdoptionSubscriptionConsumer
{
    private static final Logger LOG = LoggerFactory.getLogger(AdoptionSubscriptionConsumer.class);

    private final TaskProcessorService taskProcessorService;

    public AdoptionSubscriptionConsumer(TaskProcessorService taskProcessorService)
    {
        this.taskProcessorService = taskProcessorService;
    }

    @RabbitListener(queues = "adoptionSubscriptionQueue")
    public void receiveMessage(String message)
    {
        LOG.info("Received message from RabbitMQ: {}", message);

        try
        {
            // Convert JSON message to object
            AdoptionSubscriptionTaskInfo taskInfo = Converter.toAdoptionSubscriptionTaskInfo(message);
            LOG.info("Converted message to AdoptionSubscriptionTaskInfo: {}", taskInfo);

            // Process the task
            taskProcessorService.processTask(taskInfo);

            LOG.info("Successfully processed message for adoption ID: {}", taskInfo.adoptionId());
        } catch (Exception e)
        {
            LOG.error("Error processing message from RabbitMQ: {} - {}", message, e.getMessage(), e);
        }
    }
}