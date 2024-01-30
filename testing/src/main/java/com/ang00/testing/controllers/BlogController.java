package com.ang00.testing.controllers;

import com.ang00.testing.services.BlogService;
import com.ang00.testing.services.ResponseService;
import com.ang00.testing.utils.HttpUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping()
    public ResponseEntity<ResponseService> getEntries() {
        ResponseService response = blogService.getEntries();

        return ResponseEntity.status(HttpUtil.getHttpStatus(response)).body(response);
    }

}
