package org.ubb.email_notification_service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.core.io.Resource;
import org.ubb.email_notification_service.dto.AdoptionContent;
import org.ubb.email_notification_service.dto.AdoptionSubscriptionTaskInfo;
import org.ubb.email_notification_service.exception.DataProcessingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Converter
{
    private final static ObjectMapper mapper = new ObjectMapper();
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String toCurrentTime()
    {
        return LocalDateTime.now().format(formatter);
    }

    public static AdoptionContent toAdoptionContent(Resource resource)
    {
        try
        {
            String json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            return mapper.readValue(json, AdoptionContent.class);
        } catch (IOException e)
        {
            throw new DataProcessingException("Error converting resource to AdoptionContent: " + e.getMessage(), e);
        }
    }

    public static AdoptionSubscriptionTaskInfo toAdoptionSubscriptionTaskInfo(String json)
    {
        try
        {
            return mapper.readValue(json, AdoptionSubscriptionTaskInfo.class);
        } catch (Exception e)
        {
            throw new DataProcessingException("Failed to convert JSON to AdoptionSubscriptionTaskInfo", e);
        }
    }

    public static String toJsonString(Object object) throws JsonProcessingException
    {
        return mapper.writeValueAsString(object);
    }
}