package com.ang00.testing.services;

public class ResponseService {
    private boolean status;
    private Object response = null;
    private HttpStatus HttpStatus;
    String message;

    public enum HttpStatus {
        OK,
        BAD_REQUEST,
        HTTP_SERVER_ERROR,
        HTTP_NOT_FOUND,
        CONFLICT
    }

    public boolean getStatus() {
        return status;
    }

    public Object getResponse() {
        return response;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseService.HttpStatus getHttpStatus() {
        return HttpStatus;
    }

    public void setHttpStatus(ResponseService.HttpStatus httpStatus) {
        HttpStatus = httpStatus;
    }
}
