package org.service.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CredentialInvalidException extends RuntimeException{


    public CredentialInvalidException(String message){
        super(message);
    }
}