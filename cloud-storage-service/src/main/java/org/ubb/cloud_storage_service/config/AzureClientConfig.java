package org.ubb.cloud_storage_service.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureClientConfig
{
    @Value("${container-storage.azure.key}")
    private String containerConnectionKey;

    @Bean
    public BlobServiceClient blobServiceClient()
    {
        if (StringUtils.isBlank(containerConnectionKey))
        {
            throw new IllegalArgumentException("Cannot find Azure connection Key");
        }
        return new BlobServiceClientBuilder()
                .connectionString(containerConnectionKey)
                .buildClient();
    }
}
