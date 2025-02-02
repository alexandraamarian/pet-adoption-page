package org.ubb.cloud_storage_service.service;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.ubb.cloud_storage_service.exception.ObjectNotFoundException;
import org.ubb.cloud_storage_service.exception.ObjectStorageException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.ubb.cloud_storage_service.utils.Converter.toMimeType;

@Component
public class AzureBlobStorageClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AzureBlobStorageClient.class);
    private static final String STORAGE_CONTAINER = "adoptions-container";

    private final BlobServiceClient storageClient;

    public AzureBlobStorageClient(BlobServiceClient storageClient)
    {
        this.storageClient = storageClient;
    }

    private BlobClient resolveBlob(String userName, UUID containerId, String fileName)
    {
        String path = String.format("%s/%s/%s", userName, containerId, fileName);
        return storageClient.getBlobContainerClient(STORAGE_CONTAINER).getBlobClient(path);
    }

    public void saveFile(String userName, UUID containerId, String fileName, InputStream data)
    {
        LOGGER.info("Starting upload: userName: {}, containerId: {}, fileName: {}", userName, containerId, fileName);
        BlobClient client = resolveBlob(userName, containerId, fileName);

        if (client.exists())
        {
            LOGGER.warn("File already exists: userName: {}, containerId: {}, fileName: {}", userName, containerId, fileName);
            throw new ObjectStorageException("File already exists and cannot be replaced.");
        }

        try
        {
            BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(toMimeType(fileName));
            client.upload(data, data.available(), true);
            client.setHttpHeaders(headers);
        } catch (IOException e)
        {
            LOGGER.error("Upload failed: userName: {}, containerId: {}, fileName: {}", userName, containerId, fileName, e);
            throw new ObjectStorageException("Error storing file", e);
        }

        LOGGER.info("Upload complete: userName: {}, containerId: {}, fileName: {}", userName, containerId, fileName);
    }

    public InputStream retrieveFile(String userName, UUID containerId, String fileName)
    {
        LOGGER.info("Fetching file: userName: {}, containerId: {}, fileName: {}", userName, containerId, fileName);
        BlobClient client = resolveBlob(userName, containerId, fileName);

        if (!client.exists())
        {
            LOGGER.warn("File not found: userName: {}, containerId: {}, fileName: {}", userName, containerId, fileName);
            throw new ObjectNotFoundException("Requested file does not exist.");
        }

        InputStream fileStream = client.openInputStream();
        LOGGER.info("Fetch complete: userName: {}, containerId: {}, fileName: {}", userName, containerId, fileName);
        return fileStream;
    }

    public boolean fileExists(String userName, UUID containerId, String fileName)
    {
        LOGGER.info("Checking existence: userName: {}, containerId: {}, fileName: {}", userName, containerId, fileName);
        boolean exists = resolveBlob(userName, containerId, fileName).exists();
        LOGGER.info("Existence check complete: userName: {}, containerId: {}, fileName: {}", userName, containerId, fileName);
        return exists;
    }

    public void removeFile(String userName, UUID containerId, String fileName)
    {
        LOGGER.info("Deleting file: userName: {}, containerId: {}, fileName: {}", userName, containerId, fileName);
        BlobClient client = resolveBlob(userName, containerId, fileName);

        if (client.exists())
        {
            client.delete();
        }

        LOGGER.info("Deletion complete: userName: {}, containerId: {}, fileName: {}", userName, containerId, fileName);
    }
}
