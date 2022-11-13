package ca.bc.gov.open.icon.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class APIThrownException extends RuntimeException {
    public APIThrownException() {
        super(
                "An error response was received from server please check that your request is of valid form");
    }

    public APIThrownException(String message) {
        super(message);
    }
}
