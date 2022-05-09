package com.gallery.services;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.gallery.domain.Image;
import com.gallery.forms.ImageCreateForm;

public interface ImageService {

    List<Image> findAll();

    Image createOrUpdate(MultipartFile file, ImageCreateForm imageCreateForm, BindingResult bindingResult);

    Image edit(Image image);

    Image findById(long id);

    void deleteById(long id);
}
