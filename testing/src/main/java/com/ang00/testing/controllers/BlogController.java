package com.ang00.testing.controllers;

import com.ang00.testing.services.BlogService;
import com.ang00.testing.services.ResponseService;
import com.ang00.testing.services.UserService;
import com.ang00.testing.models.BlogModel;
import com.ang00.testing.models.UserModel;
import com.ang00.testing.utils.HttpUtil;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<ResponseService> getEntries() {
        ResponseService response = blogService.getEntries();

        return ResponseEntity.status(HttpUtil.getHttpStatus(response)).body(response);
    }

    @PostMapping()
    public ResponseEntity<ResponseService> newEntry(@RequestHeader("Authorization-Token") String token,
            @RequestParam("title") String title, @RequestParam("content") String content,
            @RequestParam(name = "img_banner", required = false) MultipartFile imgBanner) {

        ResponseService response = new ResponseService();
        ResponseService account = this.userService.getUserFromToken(token);
        UserModel author;
        if (account.getStatus()) {

            author = (UserModel) account.getResponse();

            BlogModel entry = new BlogModel();
            entry.setTitle(title);
            entry.setContent(content);
            entry.setAuthor(author);

            ResponseService newEntry = this.blogService.newEntry(entry);
            response = newEntry;

        } else {
            response = account;
        }

        if (imgBanner != null && response.getStatus()) {
            if (!imgBanner.isEmpty()) {

            }
        }

        return ResponseEntity.status(HttpUtil.getHttpStatus(response)).body(response);
    }

}
