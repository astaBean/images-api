package com.gallery.restcontroller;

import com.gallery.domain.Image;
import com.gallery.error.ApiErrorModel;
import com.gallery.exception.ApiErrorException;
import com.gallery.forms.ImageCreateForm;
import com.gallery.forms.ImageUpdateForm;
import com.gallery.notification.NotificationService;
import com.gallery.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageRestController {

    private final ImageService imageService;
    private final NotificationService notificationService;

    @Autowired
    public ImageRestController(ImageService imageService, NotificationService notificationService) {
        this.imageService = imageService;
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Image>> returnAll() {
        return new ResponseEntity<>(imageService.findAll(), HttpStatus.OK);
    }

    @PostMapping(headers = ("content-type=multipart/*"))
    public ResponseEntity<Image> create(@Valid ImageCreateForm imageCreateForm, BindingResult bindingResult, MultipartFile file) {

        if (bindingResult.hasErrors()) {
            processValidationErrors(bindingResult.getAllErrors());
        }

        final Image image = imageService.createImage(file, imageCreateForm);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @PutMapping(headers = ("content-type=multipart/*"))
    public ResponseEntity<Image> update(@Valid ImageUpdateForm imageUpdateForm, BindingResult bindingResult, MultipartFile file) {

        if (bindingResult.hasErrors()) {
            processValidationErrors(bindingResult.getAllErrors());
        }

        final Image image = imageService.updateImage(file, imageUpdateForm);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<Image> getImage(@PathVariable("uuid") String uuid) {
        // Add validation here
        final UUID identifier = UUID.fromString(uuid);
        final Image image = imageService.getImage(identifier);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{uuid}")
    public void delete(@PathVariable("uuid") String uuid) {
        final UUID identifier = UUID.fromString(uuid);
        imageService.deleteImage(identifier);
    }

    private void processValidationErrors(Iterable<? extends ObjectError> errors) {
        ApiErrorException apiErrorException = new ApiErrorException();
        errors.forEach(objectError -> {
                    final String message = objectError.getDefaultMessage();
                    notificationService.addErrorMessage(message);
                    apiErrorException.addError(new ApiErrorModel(message));
                }
        );

        throw apiErrorException;
    }

}
