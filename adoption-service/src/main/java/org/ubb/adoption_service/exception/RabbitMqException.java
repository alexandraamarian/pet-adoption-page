package org.ubb.adoption_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RabbitMqException extends RuntimeException
{
    public RabbitMqException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
