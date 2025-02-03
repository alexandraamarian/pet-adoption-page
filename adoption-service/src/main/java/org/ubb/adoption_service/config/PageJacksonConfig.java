package org.ubb.adoption_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

// copied from https://bootify.io/spring-boot/pagination-in-spring-boot-rest-api.html
@Configuration
@EnableSpringDataWebSupport(
        pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
public class PageJacksonConfig
{
}
