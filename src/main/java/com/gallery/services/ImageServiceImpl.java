package com.gallery.services;

import com.gallery.domain.Image;
import com.gallery.forms.ImageCreateForm;
import com.gallery.repository.ImageRepository;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final NotificationService notificationService;

    @Autowired
    public ImageServiceImpl(
            ImageRepository imageRepository, NotificationService notificationService) {
        this.imageRepository = imageRepository;
        this.notificationService = notificationService;
    }

    @Override
    public List<Image> findAll() {
        List<Image> images = imageRepository.findAll();

        if (CollectionUtils.isEmpty(images)) {
            notificationService.addErrorMessage("No images to return");
        }

        return images;
    }

    @Override
    public Image createOrUpdate(MultipartFile file, ImageCreateForm imageCreateForm, BindingResult bindingResult) {

        final Image image = new Image();

        if (bindingResult.hasErrors()) {
            bindingResult
                    .getAllErrors()
                    .forEach(objectError -> notificationService.addErrorMessage(objectError.getDefaultMessage()));
            return image;
        }

        try {
            final String fileLocation = "/img/gallery/" + file.getOriginalFilename();
            final Image imageToCreate = imageCreateForm.getId() != null
                    ? imageRepository.findById(imageCreateForm.getId()).orElse(image)
                    : image;
            image.setTitle(imageCreateForm.getTitle());
            image.setDescription(imageCreateForm.getDescription());
            image.setPath(fileLocation);
            Image savedImage = imageRepository.save(imageToCreate);
            notificationService.addInfoMessage("Image updated successfully");
            return savedImage;

        } catch (IllegalArgumentException e) {
            notificationService.addErrorMessage("Error occurred:" + e.getMessage());
            return image;
        }
    }

    @Override
    public Image edit(Image image) {
        Optional<Image> existingImage = imageRepository.findById(image.getId());
        if (existingImage.isPresent()) {
            return imageRepository.save(image);
        } else {
            notificationService.addErrorMessage("Image with id:" + image.getId() + " is not found");
            return null;
        }
    }

    @Override
    public Image findById(long id) {
        Optional<Image> image = imageRepository.findById(id);
        if (image.isPresent()) {
            return image.get();
        } else {
            this.notificationService.addErrorMessage("Image with id:" + id + " is not found");
            return null;
        }
    }

    @Override
    public void deleteById(long id) {
        try {
            imageRepository.deleteById(id);
            notificationService.addInfoMessage("Successfully deleted an image");
        } catch (Exception e) {
            notificationService.addErrorMessage("Image can not be deleted");
        }
    }
}
