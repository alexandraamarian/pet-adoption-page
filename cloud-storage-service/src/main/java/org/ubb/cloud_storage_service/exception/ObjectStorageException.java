package org.ubb.cloud_storage_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ObjectStorageException extends RuntimeException
{
    public ObjectStorageException(String message)
    {
        super(message);
    }

    public ObjectStorageException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
