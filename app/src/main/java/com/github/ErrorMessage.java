package com.github;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ErrorMessage extends RuntimeException {
    private final HttpStatusCode status;

    public ErrorMessage(HttpStatusCode status, String err_message)
    {
        super(err_message);
        this.status=status;
    }

    public HttpStatusCode getStatus()
    {
        return status;
    }
}
