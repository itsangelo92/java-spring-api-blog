package com.ang00.testing.utils;

import com.ang00.testing.services.ResponseService;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;

public class UserUtil {

    public static ResponseService hashPassword(String password) {
        ResponseService response = new ResponseService();
        try {
            String hashedPassword = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
            response.setStatus(true);
            response.setResponse(hashedPassword);
            response.setMessage("The password has been hashed.");
            response.setHttpStatus(ResponseService.HttpStatus.OK);
        } catch (Exception err) {
            response.setStatus(false);
            response.setMessage("There was some problems while trying to hash the password. Error: \n" + err);
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
        }

        return response;
    }

    public static ResponseService generateToken(String password) {

        ResponseService response = new ResponseService();

        try {
            String currentTimeStamp = Timestamp.from(Instant.now()).toString();
            ResponseService hashResponse = hashPassword(password);
            if(hashResponse.getStatus()) {
                String hashedPassowrd = hashResponse.getResponse().toString();
                String tokenToHash = currentTimeStamp + hashedPassowrd;
                String hashedToken = Hashing.sha256().hashString(tokenToHash, StandardCharsets.UTF_8).toString();

                response.setStatus(true);
                response.setResponse(hashedToken);
                response.setMessage("The token has been generated");
                response.setHttpStatus(ResponseService.HttpStatus.OK);
                return response;
            }
            hashResponse.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
            return hashResponse;
        } catch(Exception err) {
            response.setStatus(false);
            response.setMessage("There was sobre problems while generating the token. Error: \n" + err);
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
        }

        return response;

    }

}
