package com.gallery.restcontroller;

import com.gallery.domain.Image;
import com.gallery.forms.ImageCreateForm;
import com.gallery.services.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/image")
public class ImagesRestController {

    private final ImageService imageService;

    @Autowired
    public ImagesRestController(ImageService imageService) {this.imageService = imageService;}

    @GetMapping(value = "/all")
    public List<Image> returnAll() {
        return imageService.findAll();
    }

    @PostMapping(value = "/createOrUpdate", headers = ("content-type=multipart/*"))
    public Image create(@Valid ImageCreateForm imageCreateForm, BindingResult bindingResult, MultipartFile file) {
        return imageService.createOrUpdate(file, imageCreateForm, bindingResult);
    }

    @GetMapping(value = "/{id}")
    public Image findById(@PathVariable("id") Long id) {
        return imageService.findById(id);
    }

    @GetMapping(value = "/delete/{id}")
    public void delete(@PathVariable("id") Long id) {
        imageService.deleteById(id);
    }


}
