package com.ang00.testing.services;

import com.ang00.testing.models.UserModel;
import com.ang00.testing.repositories.IUserRepository;
import com.ang00.testing.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    IUserRepository userRepository;

    public ResponseService getUsers() {
        ResponseService response = new ResponseService();
        try {
            ArrayList<UserModel> users = (ArrayList<UserModel>) this.userRepository.findAll();
            response.setStatus(true);
            response.setResponse(users);
            response.setMessage("Aquí tienes la lista con todos los usuarios");
            if (response.getResponse().toString().equals("[]")) {
                response.setMessage("No hay usuarios registrados.");
                response.setResponse(null);
            }
            response.setHttpStatus(ResponseService.HttpStatus.OK);
        } catch (Exception err) {
            response.setStatus(false);
            response.setMessage("There was some problems while saving the user: \n" + err);
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
        }
        return response;
    }

    public ResponseService validateAccount(UserModel user) {
        ResponseService response = new ResponseService();
        UserModel validateUser = this.userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());

        if (validateUser != null) {
            response.setStatus(true);
            response.setResponse(validateUser);
            response.setMessage("Inicio de sesión validada...");
            response.setHttpStatus(ResponseService.HttpStatus.OK);
        } else {
            response.setStatus(false);
            response.setMessage("Email o contraseña incorrectos...");
            response.setHttpStatus(ResponseService.HttpStatus.CONFLICT);
        }

        return response;
    }

    public ResponseService saveUser(UserModel user) {
        ResponseService response = new ResponseService();
        String email = user.getEmail();

        UserModel existsUser = userRepository.findByEmail(email);
        if (existsUser == null) {
            ResponseService hashResponse = UserUtil.hashPassword(user.getPassword());
            if (hashResponse.getStatus()) {
                user.setPassword(hashResponse.getResponse().toString());
                ResponseService generateToken = UserUtil.generateToken(user.getPassword());
                if (generateToken.getStatus()) {
                    user.setToken(generateToken.getResponse().toString());
                    try {
                        UserModel newUser = this.userRepository.save(user);
                        response.setStatus(true);
                        response.setResponse(newUser);
                        response.setMessage("The user has been saved.");
                        response.setHttpStatus(ResponseService.HttpStatus.OK);

                        return response;
                    } catch (Exception err) {
                        response.setStatus(false);
                        response.setMessage("There was some problems while saving the user: \n" + err);
                        response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
                    }
                } else {
                    response = generateToken;
                    response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
                }
            } else {
                response = hashResponse;
                response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
            }
        } else {
            response.setStatus(false);
            response.setMessage("Ya existe una cuenta con ese correo, usa otro correo en lugar.");
            response.setHttpStatus(ResponseService.HttpStatus.CONFLICT);
        }

        return response;
    }

    public ResponseService getById(Long id) {

        ResponseService response = new ResponseService();

        try {
            UserModel user = userRepository.findById(id).orElse(null);

            if (user != null) {
                response.setStatus(true);
                response.setResponse(user);
                response.setMessage("Aquí tienes la información del usuario: " + user.getFirstName() + " "
                        + user.getLastName() + " con correo: " + user.getEmail());
                response.setHttpStatus(ResponseService.HttpStatus.OK);
            } else {
                response.setStatus(false);
                response.setMessage("No existe ningún usuario con esa ID.");
                response.setHttpStatus(ResponseService.HttpStatus.HTTP_NOT_FOUND);
            }
        } catch (Exception err) {
            response.setStatus(false);
            response.setMessage("Hubo un error al tratar de conseguir la información con la id: " + id);
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
        }
        return response;
    }

    public ResponseService updateById(Long id, UserModel user) {

        ResponseService response = new ResponseService();
        try {
            Optional<UserModel> optionalOldUser = Optional.ofNullable(userRepository.findById(id).orElse(null));
            if (optionalOldUser.isPresent() || optionalOldUser != null) {
                try {
                    user.setPassword(optionalOldUser.get().getPassword());
                    user.setToken(optionalOldUser.get().getToken());

                    try {
                        UserModel newUser = userRepository.save(user);

                        response.setStatus(true);
                        response.setResponse(newUser);
                        response.setMessage("Se ha actualizado la información del usuario: " + user.getFirstName() + " "
                                + user.getLastName());
                        response.setHttpStatus(ResponseService.HttpStatus.OK);
                    } catch (Exception err) {
                        response.setStatus(false);
                        response.setMessage("There was some problems while trying to update the user: \n" + err);
                        response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
                    }

                } catch (Exception err) {
                    response.setStatus(false);
                    response.setMessage("There was some problems while trying to update the user: \n" + err);
                    response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
                }
            }
            response.setStatus(false);
            response.setMessage("No se encontró a ese usuario ");
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_NOT_FOUND);
        } catch (Exception err) {
            response.setStatus(false);
            response.setMessage("There was some problems while trying to get information from the user: \n" + err);
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
        }

        return response;
    }

    public ResponseService deleteUser(Long id) {
        ResponseService response = new ResponseService();
        try {
            UserModel user = userRepository.findById(id).orElse(null);
            if (user != null) {
                try {
                    this.userRepository.deleteById(id);
                    response.setStatus(true);
                    response.setMessage("Usuario: " + user.getFirstName() + " " + user.getLastName()
                            + " ha sido borrado con éxito");
                    response.setHttpStatus(ResponseService.HttpStatus.OK);
                } catch (Exception err) {
                    response.setStatus(false);
                    response.setMessage("There was somre problems while trying to delete the user: "
                            + user.getFirstName() + " " + user.getLastName() + "\nError: \n" + err);
                    response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
                }
            } else {
                response.setStatus(false);
                response.setMessage("No existe ningún usuario con esa ID.");
                response.setHttpStatus(ResponseService.HttpStatus.HTTP_NOT_FOUND);
            }
        } catch (Exception err) {
            response.setStatus(false);
            response.setMessage("There was some problem trying to fetch information from the user: \n" + err);
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
        }

        return response;
    }

    public ResponseService updateUserAvatar(String imageName, UserModel userInfo) {
        ResponseService response = new ResponseService();
        ResponseService newImageName = UserUtil.generateToken(imageName);
        if (newImageName.getStatus()) {
            String newRouteImage = newImageName.getResponse().toString();
            String[] format = imageName.split("\\.");
            String fileExtension = format[format.length - 1];
            userInfo.setAvatar(newRouteImage + "." + fileExtension);
            try {
                UserModel updatedUser = this.userRepository.save(userInfo);
                response.setStatus(true);
                response.setResponse(updatedUser);
                response.setMessage("Se ha actualizado el perfil del usuario...");
                response.setHttpStatus(ResponseService.HttpStatus.OK);
            } catch (Exception err) {
                response.setStatus(false);
                response.setMessage(
                        "There was some problems while trying to update the avatar in the database... Error: \n"
                                + err);
                response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
            }
        } else {
            response = newImageName;
        }

        return response;
    }

    public ResponseService getUserFromToken(String token) {
        ResponseService response = new ResponseService();
        try {
            UserModel user = userRepository.findByToken(token);
            if (user != null) {
                response.setStatus(true);
                response.setResponse(user);
                response.setMessage("Esta es la información encontrada con el token: " + token);
                response.setHttpStatus(ResponseService.HttpStatus.OK);
            } else {
                response.setStatus(false);
                response.setMessage("No se pudo encontrar ninguna cuenta con el token: " + token);
                response.setHttpStatus(ResponseService.HttpStatus.HTTP_NOT_FOUND);
            }

        } catch (Exception err) {
            response.setStatus(false);
            response.setMessage(
                    "There was some problems while trying to get the information of the account with token: " + token
                            + ".\nError:\n" + err);
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
        }

        return response;
    }
}