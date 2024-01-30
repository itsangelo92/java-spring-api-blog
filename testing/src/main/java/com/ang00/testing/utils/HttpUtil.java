package com.ang00.testing.utils;

import org.springframework.http.HttpStatus;

import com.ang00.testing.services.ResponseService;

public class HttpUtil {
    public static HttpStatus getHttpStatus(ResponseService response) {
        switch (response.getHttpStatus()) {
            case OK:
                return HttpStatus.OK;
            case BAD_REQUEST:
                return HttpStatus.BAD_REQUEST;
            case HTTP_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case CONFLICT:
                return HttpStatus.CONFLICT;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
