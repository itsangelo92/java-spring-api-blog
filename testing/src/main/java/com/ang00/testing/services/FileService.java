package com.ang00.testing.services;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import com.ang00.testing.models.UserModel;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public record FileService(ResourceLoader resourceLoader, @Value("${storage.directory}") String storageDirectory) {

    public ResponseService getImage(String imageName) {
        ResponseService response = new ResponseService();

        try {
            String imagePath = storageDirectory + File.separator + imageName;
            Resource resource = new FileSystemResource(imagePath);
            File file = resource.getFile();
            if (resource.exists()) {

                byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());

                response.setStatus(true);
                response.setResponse(imageBytes);
                response.setMessage("Imagen encontrada correctamente...");
                response.setHttpStatus(ResponseService.HttpStatus.OK);
            } else {
                response.setStatus(false);
                response.setMessage("No existe la imagen en el servidor: " + file);
                response.setHttpStatus(ResponseService.HttpStatus.HTTP_NOT_FOUND);
            }
        } catch (Exception err) {
            response.setStatus(false);
            response.setMessage("Hubo un error al intentar obtener la imagen: " + err.getMessage());
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
        }

        return response;
    }

    public ResponseService saveImage(MultipartFile image, UserModel user) {
        ResponseService response = new ResponseService();

        try {
            String[] format = image.getOriginalFilename().split("\\.");
            if (format.length > 1) {
                String fileExtension = format[format.length - 1];
                if (fileExtension.equals("png") || fileExtension.equals("jpg") || fileExtension.equals("jpeg")) {
                    File imagesFolder = new File(storageDirectory + File.separator);
                    if (!imagesFolder.exists()) {
                        imagesFolder.mkdirs();
                    }

                    byte[] imageBytes = image.getBytes();
                    String avatar = user.getAvatar();
                    Path imagePath = Paths.get(storageDirectory + File.separator, avatar);
                    Files.write(imagePath, imageBytes);

                    response.setStatus(true);
                    response.setMessage("La imagen se ha cargado correctamente en la carpeta 'images',");
                    response.setHttpStatus(ResponseService.HttpStatus.OK);
                } else {
                    response.setStatus(false);
                    response.setMessage("Solo se admiten archivos .png, .jpg y .jpeg");
                    response.setHttpStatus(ResponseService.HttpStatus.CONFLICT);
                }
            } else {
                response.setStatus(false);
                response.setMessage("No se pudo determinar el formato.");
                response.setHttpStatus(ResponseService.HttpStatus.CONFLICT);
            }
        } catch (Exception err) {
            response.setStatus(false);
            response.setMessage("There were some problems while trying to save the image... Error: \n" + err);
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
        }

        return response;
    }

    public ResponseService deleteImage(String avatarName) {
        ResponseService response = new ResponseService();

        File avatar = new File(storageDirectory + File.separator + avatarName);
        if (avatar.exists()) {
            if (avatar.delete()) {
                response.setStatus(true);
                response.setMessage("Se ha borrado el archivo: " + storageDirectory + "/" + avatarName);
                response.setHttpStatus(ResponseService.HttpStatus.OK);
            } else {
                response.setStatus(false);
                response.setMessage(
                        "No se ha podido borrar el archivo: " + storageDirectory + "/" + avatarName);
                response.setHttpStatus(ResponseService.HttpStatus.HTTP_SERVER_ERROR);
            }
        } else {
            response.setStatus(true);
            response.setMessage(
                    "La imagen: " + storageDirectory + "/" + avatarName + " no existe en el servidor");
            response.setHttpStatus(ResponseService.HttpStatus.HTTP_NOT_FOUND);
        }

        return response;
    }

}
