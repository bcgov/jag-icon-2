package ca.bc.gov.open.icon.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class RestTemplateException extends RuntimeException {
    public RestTemplateException(String message) {
        super(message);
    }
}
