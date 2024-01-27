package com.ang00.testing.controllers;

import com.ang00.testing.models.UserModel;
import com.ang00.testing.services.FileService;
import com.ang00.testing.services.UserService;
import com.ang00.testing.services.ResponseService;

import com.ang00.testing.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @GetMapping()
    public ResponseEntity<ResponseService> getUsers() {

        ResponseService response = userService.getUsers();

        return ResponseEntity.status(getHttpStatus(response)).body(response);
    }

    @PostMapping(path = "/sign-up")
    public ResponseEntity<ResponseService> saveUsers(@RequestParam("first_name") String firstName,
            @RequestParam("last_name") String lastName, @RequestParam("email") String email,
            @RequestParam("password") String password) {
        UserModel user = new UserModel();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);

        ResponseService response = userService.saveUser(user);

        return ResponseEntity.status(getHttpStatus(response)).body(response);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ResponseService> login(@RequestParam("email") String email,
            @RequestParam("password") String password) {
        ResponseService response;
        UserModel user = new UserModel();
        ResponseService hashedPassword = UserUtil.hashPassword(password);
        if (hashedPassword.getStatus()) {
            user.setEmail(email);
            user.setPassword(hashedPassword.getResponse().toString());
            ResponseService userValidated = this.userService.validateAccount(user);
            response = userValidated;
        } else {
            response = hashedPassword;
        }

        return ResponseEntity.status(getHttpStatus(response)).body(response);
    }

    @GetMapping(path = "/user/{id}")
    public ResponseEntity<ResponseService> getUserById(@PathVariable("id") Long id) {

        ResponseService response = userService.getById(id);

        return ResponseEntity.status(getHttpStatus(response)).body(response);

    }

    @GetMapping(path = "/user/token-information")
    public ResponseEntity<ResponseService> getAccountFromToken(@RequestHeader("Authorization-Token") String token) {
        ResponseService response = userService.getUserFromToken(token);

        return ResponseEntity.status(getHttpStatus(response)).body(response);
    }

    @PutMapping(path = "/user/{id}")
    public ResponseEntity<ResponseService> updateUseById(@RequestParam("first_name") String firstName,
            @RequestParam("last_name") String lastName, @RequestParam("email") String email,
            @PathVariable("id") Long id) {

        UserModel user = new UserModel();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        ResponseService updateUser = this.userService.updateById(id, user);
        return ResponseEntity.status(getHttpStatus(updateUser)).body(updateUser);
    }

    @PutMapping("/user/update-avatar")
    public ResponseEntity<ResponseService> updateAvatar(@RequestHeader("Authorization-Token") String token,
            @RequestParam("file") MultipartFile image) {
        ResponseService response = new ResponseService();
        ResponseService userInformation = this.userService.getUserFromToken(token);
        if (userInformation.getStatus()) {
            UserModel userData = (UserModel) userInformation.getResponse();
            String oldAvatarName = userData.getAvatar().toString();
            String imagePath = image.getOriginalFilename();
            boolean isDefaultAvatar = "default.png".equals(oldAvatarName);
            ResponseService updateAvatar = this.userService.updateUserAvatar(imagePath,
                    (UserModel) userInformation.getResponse());
            if (updateAvatar.getStatus()) {
                boolean isDeleted = true;
                if (!isDefaultAvatar) {
                    ResponseService deletedImage = this.fileService.deleteImage(oldAvatarName);
                    if (!deletedImage.getStatus()) {
                        isDeleted = false;
                        response = deletedImage;
                    }
                }

                if (isDeleted) {
                    ResponseService updateImage = this.fileService.saveImage(image,
                            (UserModel) userInformation.getResponse());
                    if (updateImage.getStatus()) {
                        response.setStatus(true);
                        response.setMessage("Se ha actualizado la imagen del usuario...");
                        response.setHttpStatus(ResponseService.HttpStatus.OK);
                    } else {
                        response = updateImage;
                    }
                }
            } else {
                response = updateAvatar;
            }
        } else {
            response = userInformation;
        }

        return ResponseEntity.status(getHttpStatus(response)).body(response);
    }

    @GetMapping(path = "/user/{userId}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable("userId") Long userId) {

        ResponseService userInformation = this.userService.getById(userId);
        ResponseEntity<byte[]> httpResponse = null;
        if (!userInformation.getStatus()) {
            return ResponseEntity.status(500).build();
        }

        UserModel user = (UserModel) userInformation.getResponse();
        String avatar = user.getAvatar();
        ResponseService response = this.fileService.getImage(avatar);
        if (getHttpStatus(response) == HttpStatus.OK) {
            if (response.getStatus()) {
                byte[] imageBytes = (byte[]) response.getResponse();

                HttpHeaders headers = new HttpHeaders();
                String[] format = avatar.split("\\.");
                String imgFormat = format[format.length - 1];
                if (imgFormat.equals("png")) {
                    headers.setContentType(MediaType.IMAGE_PNG);
                } else if (imgFormat.equals("jpg") || imgFormat.equals("jpeg")) {
                    headers.setContentType(MediaType.IMAGE_JPEG);
                }
                headers.setContentLength(imageBytes.length);

                httpResponse = new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

            } else {
                httpResponse = ResponseEntity.status(404).build();
            }
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.set("ERROR", response.getMessage());
            httpResponse = new ResponseEntity<>(null, headers, getHttpStatus(response));
        }

        return httpResponse;
    }

    @DeleteMapping(path = "/user/{id}")
    public ResponseEntity<ResponseService> deleteById(@PathVariable("id") Long id) {
        ResponseService response = this.userService.deleteUser(id);

        return ResponseEntity.status(getHttpStatus(response)).body(response);
    }

    // Get the HttpStatus from ResponseService OBJECT
    private HttpStatus getHttpStatus(ResponseService response) {
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
