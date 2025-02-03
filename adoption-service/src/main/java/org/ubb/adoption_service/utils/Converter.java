package org.ubb.adoption_service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.core.io.Resource;
import org.ubb.adoption_service.api.AdoptionContent;
import org.ubb.adoption_service.exception.DataProcessingException;

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
            throw new DataProcessingException("Error converting resource to AdoptionContent: " + e.getMessage(), e); // Re-throw as IOException or custom exception
        }
    }

    public static String toJsonString(Object object) throws JsonProcessingException
    {
        return mapper.writeValueAsString(object);
    }
}
