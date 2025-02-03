package org.ubb.adoption_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.ubb.adoption_service.api.AdoptionSubscriptionTaskInfo;
import org.ubb.adoption_service.config.RabbitMqProperties;
import org.ubb.adoption_service.exception.RabbitMqException;
import org.ubb.adoption_service.utils.Converter;

@Component
public class RabbitMqProducer
{
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties rabbitMqProperties;

    public RabbitMqProducer(RabbitTemplate rabbitTemplate, RabbitMqProperties rabbitMqProperties)
    {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMqProperties = rabbitMqProperties;
    }

    public void sendTask(AdoptionSubscriptionTaskInfo taskInfo)
    {
        try
        {
            String jsonData = Converter.toJsonString(taskInfo);
            rabbitTemplate.convertAndSend(rabbitMqProperties.getExchange(), rabbitMqProperties.getRoutingKey(), jsonData);
        } catch (JsonProcessingException e)
        {
            throw new RabbitMqException("Unable to convert task info to JSON", e);
        }
    }
}
