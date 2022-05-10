package com.gallery.services;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.gallery.domain.Image;
import com.gallery.forms.ImageCreateForm;
import com.gallery.forms.ImageUpdateForm;

public interface ImageService {

    List<Image> findAll();

    Image createImage(MultipartFile file, ImageCreateForm imageCreateForm);

    Image updateImage(MultipartFile file, ImageUpdateForm imageCreateForm);

    Image getImage(UUID uuid);

    void deleteImage(UUID uuid);
}
