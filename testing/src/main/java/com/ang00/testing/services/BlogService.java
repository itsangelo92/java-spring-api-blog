package com.ang00.testing.services;

import com.ang00.testing.models.BlogModel;
import com.ang00.testing.repositories.IBlogRepository;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogService {

    @Autowired
    IBlogRepository blogRepository;

    public ResponseService getEntries() {
        ResponseService response = new ResponseService();

        try {

            ArrayList<BlogModel> entries = (ArrayList<BlogModel>) this.blogRepository.findAll();

            response.setStatus(true);
            response.setResponse(entries);
            response.setMessage("Aquí tienes todas las entradas");

            if (response.getResponse().toString() == "[]") {
                response.setMessage("No hay ninguna entrada todavía...");
                response.setResponse(null);
            }

            response.setHttpStatus(ResponseService.HttpStatus.OK);

        } catch (Exception err) {
            response.setStatus(false);
            response.setMessage("There was some problems while getting the entries list: \n" + err);
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
        }

        return response;
    }

    public ResponseService newEntry(String token, BlogModel entry) {
        UserService user = new UserService();
        ResponseService response = new ResponseService();
        ResponseService accountInformation = user.getUserFromToken(token);
        
        if(accountInformation.getStatus()) {
           
        } else {
            response = accountInformation;
        }

        return response;
    }

}
