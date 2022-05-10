package com.gallery.services;

import com.gallery.domain.Image;
import com.gallery.error.DatabaseOperationError;
import com.gallery.forms.ImageCreateForm;
import com.gallery.forms.ImageUpdateForm;
import com.gallery.repository.ImageRepository;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        Iterable<Image> imagesIterable = imageRepository.findAll();
        List<Image> images =
                StreamSupport.stream(imagesIterable.spliterator(), false)
                        .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(images)) {
            notificationService.addErrorMessage("No images found");
        }

        return images;
    }

    @Override
    public Image createImage(MultipartFile file, ImageCreateForm imageCreateForm) {

        final String fileLocation = "/img/gallery/" + file.getOriginalFilename();
        final Image imageToCreate = Image.builder()
                .title(imageCreateForm.getTitle())
                .description(imageCreateForm.getDescription())
                .path(fileLocation)
                .build();

        try {
            Image createdImage = imageRepository.save(imageToCreate);
            notificationService.addInfoMessage("Image has been created successfully");
            return createdImage;
        } catch (IllegalArgumentException e) {
            notificationService.addErrorMessage("Database operation failed with message: " + e.getMessage());
            throw new DatabaseOperationError("Database operation failed");
        }
    }

    @Override
    public Image updateImage(MultipartFile file, ImageUpdateForm imageUpdateForm) {

        final String fileLocation = "/img/gallery/" + file.getOriginalFilename();
        final Optional<Image> imageFromDb = imageRepository.findById(imageUpdateForm.getUuid());

        if (!imageFromDb.isPresent()) {
            notificationService.addErrorMessage("Image has not been found in database");
            return null;
        }

        final Image imageToUpdate = imageFromDb.get();

        try {
            // Maybe introduce mapper to easier obtain image
            imageToUpdate.setTitle(imageUpdateForm.getTitle());
            imageToUpdate.setDescription(imageUpdateForm.getDescription());
            imageToUpdate.setPath(fileLocation);
            Image savedImage = imageRepository.save(imageToUpdate);
            notificationService.addInfoMessage("Image updated successfully");
            return savedImage;

        } catch (IllegalArgumentException e) {
            notificationService.addErrorMessage("Error occurred: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Image getImage(UUID uuid) {
        Optional<Image> image = imageRepository.findById(uuid);
        if (image.isPresent()) {
            return image.get();
        } else {
            this.notificationService.addErrorMessage(String.format("Image with uuid [%s] is not found", uuid));
            return null;
        }
    }

    @Override
    public void deleteImage(UUID uuid) {
        try {
            imageRepository.deleteById(uuid);
            notificationService.addInfoMessage(String.format("Successfully deleted an image with uuid [%s]", uuid));
        } catch (Exception e) {
            notificationService.addErrorMessage("Image can not be deleted");
        }
    }
}
