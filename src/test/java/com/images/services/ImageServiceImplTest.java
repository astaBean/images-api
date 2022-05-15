package com.images.services;

import com.images.domain.Image;
import com.images.exception.DatabaseOperationException;
import com.images.forms.ImageCreateForm;
import com.images.forms.ImageUpdateForm;
import com.images.notification.NotificationService;
import com.images.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ImageServiceImplTest {
    private ImageServiceImpl service;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        service = new ImageServiceImpl(imageRepository, notificationService);
    }

    @Test
    void testFindAll_whenImagesReturnedFromRepository_shouldReturnEmptyList() {
        final List<Image> emptyListReturned = Collections.emptyList();

        given(imageRepository
                .findAll())
                .willReturn(emptyListReturned);

        final List<Image> result = service.findAll();

        // Then
        assertThat(result, is(empty()));
        verify(notificationService).addErrorMessage("No images found");

    }

    @Test
    void testFindAll_whenImagesReturnedFromRepository_shouldReturnListOfImages() {
        final Image image1 = Image.builder().uuid(UUID.randomUUID()).build();
        final Image image2 = Image.builder().uuid(UUID.randomUUID()).build();

        final List<Image> listReturned = Arrays.asList(image1, image2);

        given(imageRepository
                .findAll())
                .willReturn(listReturned);

        final List<Image> result = service.findAll();

        assertThat(result, hasItems(image1, image2));
        verify(notificationService, never()).addErrorMessage("No images found");

    }

    @Test
    void testCreateImage_whenImageFailedToSave_thenDatabaseOperationErrorThrown() {
        final MultipartFile fileMock = new MockMultipartFile("mockFile", "whatever".getBytes());
        final ImageCreateForm imageCreateForm = ImageCreateForm.builder().build();

        final IllegalArgumentException exceptionToThrow = new IllegalArgumentException("some message here");

        given(imageRepository
                .save(any(Image.class)))
                .willThrow(exceptionToThrow);

        final DatabaseOperationException result = assertThrows(DatabaseOperationException.class, () ->
                service.createImage(fileMock, imageCreateForm));

        assertThat(result.getMessage(), equalTo("Database operation failed"));
        verify(notificationService).addErrorMessage("Database operation failed with message: " + exceptionToThrow.getMessage());
    }

    @Test
    void testCreateImage_whenImageSavedSuccessfully_thenCreatedImageReturned() {
        final MultipartFile fileMock = new MockMultipartFile("mockFile", "whatever".getBytes());
        final ImageCreateForm imageCreateForm = ImageCreateForm.builder().build();

        final Image imageReturned = Image.builder().description("a description").build();

        given(imageRepository
                .save(any(Image.class)))
                .willReturn(imageReturned);

        final Image result = service.createImage(fileMock, imageCreateForm);
        assertThat(result.getDescription(), equalTo(imageReturned.getDescription()));
        verify(notificationService).addInfoMessage("Image has been created successfully");
    }

    @Test
    void testUpdateImage_whenImageDoesNotExist_thenNullValueReturned() {
        final MultipartFile fileMock = new MockMultipartFile("mockFile", "whatever".getBytes());
        final ImageUpdateForm imageUpdateForm = ImageUpdateForm.builder().build();

        final Optional<Image> imageReturned = Optional.empty();

        given(imageRepository
                .findById(imageUpdateForm.getUuid()))
                .willReturn(imageReturned);

        final Image result = service.updateImage(fileMock, imageUpdateForm);

        assertThat(result, is(nullValue()));
        verify(notificationService).addErrorMessage("Image has not been found in database");
    }

    @Test
    void testUpdateImage_whenImageFailedToSave_thenNullReturned() {
        final MultipartFile fileMock = new MockMultipartFile("mockFile", "whatever".getBytes());
        final ImageUpdateForm imageUpdateForm = ImageUpdateForm.builder().build();

        final Optional<Image> imageReturned = Optional.of(Image.builder().uuid(UUID.randomUUID()).build());
        final IllegalArgumentException exceptionThrown = new IllegalArgumentException("message");

        given(imageRepository
                .findById(imageUpdateForm.getUuid()))
                .willReturn(imageReturned);

        given(imageRepository.
                save(any(Image.class)))
                .willThrow(exceptionThrown);

        final Image result = service.updateImage(fileMock, imageUpdateForm);
        assertThat(result, is(nullValue()));
        verify(notificationService).addErrorMessage("Error occurred: " + exceptionThrown.getMessage());
    }

    @Test
    void testUpdateImage_whenImageSavesToDatabase_thenEditedImageReturned() {
        final UUID imageUuid = UUID.randomUUID();
        final MultipartFile fileMock = new MockMultipartFile("mockFile", "whatever".getBytes());

        final ImageUpdateForm imageUpdateForm = ImageUpdateForm.builder()
                .title("potato")
                .description("it is a veg")
                .uuid(imageUuid)
                .build();

        final Image imageReturnedFromDb = Image.builder()
                .uuid(imageUuid)
                .build();

        final Image updatedImageReturned = Image.builder()
                .description("totally different image")
                .build();

        given(imageRepository
                .findById(imageUpdateForm.getUuid()))
                .willReturn(Optional.of(imageReturnedFromDb));

        given(imageRepository
                .save(imageReturnedFromDb))
                .willReturn(updatedImageReturned);

        final Image result = service.updateImage(fileMock, imageUpdateForm);

        assertThat(result, is(notNullValue()));
        verify(notificationService, never()).addErrorMessage(anyString());
    }

    @Test
    void testGetImage_whenImageExistsInDatabase_thenImageReturned() {
        final UUID uuid = UUID.randomUUID();

        final Image imageReturnedFromDb = Image.builder()
                .uuid(uuid)
                .build();

        given(imageRepository
                .findById(uuid))
                .willReturn(Optional.of(imageReturnedFromDb));

        final Image result = service.getImage(uuid);

        assertThat(result, equalTo(imageReturnedFromDb));
    }

    @Test
    void testGetImage_whenImageNotFoundInDatabase_thenNullValueReturned() {
        final UUID uuid = UUID.randomUUID();

        final Image imageReturnedFromDb = null;

        given(imageRepository
                .findById(uuid))
                .willReturn(Optional.ofNullable(imageReturnedFromDb));

        final Image result = service.getImage(uuid);

        assertThat(result, is(nullValue()));
        verify(notificationService).addErrorMessage(String.format("Image with uuid [%s] is not found", uuid));
    }

    @Test
    void testDeleteImage_whenUuidIsDefined_thenImageDeletedAndInfoNotificationAdded() {
        final UUID uuid = UUID.randomUUID();

        service.deleteImage(uuid);

        verify(notificationService).addInfoMessage(String.format("Successfully deleted an image with uuid [%s]", uuid));
        verify(notificationService, never()).addErrorMessage("Image can not be deleted");
    }

    @Test
    void testDeleteImage_whenDataIntegrityViolationException_thenErrorNotificationAddedAndDatabaseOperationExceptionIsThrown() {
        final UUID uuid = null;
        final DataIntegrityViolationException exceptionThrown = new DataIntegrityViolationException("message");

        doThrow(exceptionThrown)
                .when(imageRepository)
                .deleteById(uuid);

        final DatabaseOperationException result = assertThrows(DatabaseOperationException.class, () ->
                service.deleteImage(uuid));

        verify(notificationService, never()).addInfoMessage(anyString());
        verify(notificationService).addErrorMessage("Image could not be deleted - reason [message]");
        assertThat(result.getMessage(), equalTo("Image could not be deleted"));
    }

}
