package com.images.services;

import com.images.domain.Image;
import com.images.forms.ImageCreateForm;
import com.images.forms.ImageUpdateForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    List<Image> findAll();

    Image createImage(MultipartFile file, ImageCreateForm imageCreateForm);

    Image updateImage(MultipartFile file, ImageUpdateForm imageCreateForm);

    Image getImage(UUID uuid);

    void deleteImage(UUID uuid);
}
