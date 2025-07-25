package org.service.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)

public class ResourceNotFoundException extends RuntimeException{


    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue){
       super(String.format("%s not found with given input data %s: '%s'",resourceName,fieldName,fieldValue));
    }
}
