package com.gallery.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.gallery.domain.Image;
import com.gallery.forms.ImageCreateForm;
import com.gallery.repository.ImageRepository;

@ExtendWith(SpringExtension.class)
class ImageServiceImplTest {

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private NotificationService notificationService;

    @Test
    void shouldReturnImages() {
        // Given
        List<Image> imagesToBeReturned = Collections.singletonList(Image.builder().build());

        given(imageRepository
                .findAll())
                .willReturn(imagesToBeReturned);

        // When
        List<Image> result = imageService.findAll();

        // Then
        assertThat(result, equalTo(imagesToBeReturned));
        assertThat(result.size(), equalTo(imagesToBeReturned.size()));
    }

    @Test
    void shouldReturnNullWhenNoImagesFound() {
        // Given
        List<Image> imagesToBeReturned = Collections.singletonList(Image.builder().build());

        // When
        List<Image> result = imageService.findAll();

        // Then
        //        assertThat(result, is(notNullValue()));
        verify(notificationService).addErrorMessage(anyString());
    }

    @Test
    void shouldReturnNullWhenThereIsValidationErrorsForImageCreationForm() {
        // Given
        final MultipartFile fileMock = new MockMultipartFile("mockFile", "whatever".getBytes());
        final ImageCreateForm imageCreateForm = new ImageCreateForm();
        final Image image = Image.builder()
                .title("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .build();

        final BindingResult errors = new BeanPropertyBindingResult(image, "image");

        // When
        final Image resultImage = imageService.createOrUpdate(fileMock, imageCreateForm, errors);

        // Then
        verify(notificationService, times(1)).addErrorMessage("Image updated successfully");
        assertThat(resultImage, equalTo(true));
    }

    @Test
    void shouldReturnErrorMessageAndSuccessFalse() {
        // Given
        given(imageRepository.findAll()).
                willReturn(null);

        // When
        final List<Image> result = imageService.findAll();

        // Then
        //		assertNull(result.getObject());
        //		assertThat(result.getMessage(), equalTo("No images to return"));
        //		assertFalse(result.isSuccess());
    }

    @Test
    void shouldAddAnImage() {
        // Given
        final MockMultipartFile csvFile = new MockMultipartFile("csvFile",
                "file.jpg", "text/plain", "".getBytes());

        final ImageCreateForm imageCreateForm = new ImageCreateForm();
        imageCreateForm.setDescription("Any");
        imageCreateForm.setTitle("Some title");

        given(imageRepository.save(any(Image.class))).
                willReturn(null);

        // When
        final Image result = imageService.createOrUpdate(csvFile, imageCreateForm, null);

        // Then
        //		assertNull(result.getObject());
        //		assertThat(result.getMessage(), equalTo("Image added successfully"));
        //		assertTrue(result.isSuccess());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAddingImage() {
        // Given
        final MockMultipartFile csvFile = new MockMultipartFile("csvFile",
                "file.jpg", "text/plain", "".getBytes());

        final ImageCreateForm imageCreateForm = new ImageCreateForm();
        imageCreateForm.setDescription("Any");
        imageCreateForm.setTitle("Some title");

        doThrow(IllegalArgumentException.class)
                .when(imageRepository)
                .save(any(Image.class));

        // When
        //		Image result = imageService.add(csvFile, imageCreateForm);

        // Then
        //		assertNull(result.getObject());
        //		assertTrue(result.getMessage().contains("Error occured:"));
        //		assertFalse(result.isSuccess());
    }

    @Test
    void shouldReturnEditedImages() {
        // Given
        final Image image = Image.builder().build();
        final Image imageReturned = Image.builder().build();

        given(imageRepository.findById(any(Long.class)))
                .willReturn(Optional.ofNullable(image));
        given(imageRepository.save(any(Image.class)))
                .willReturn(imageReturned);

        // When
        final Image result = imageService.edit(image);
        //Image imageResult = (Image) result.getObject();

        // Then
        //		assertThat(imageResult, samePropertyValuesAs(imageReturned));
        //		assertThat(imageResult, equalTo(imageReturned));
        //		assertNull(result.getMessage());
        //		assertTrue(result.isSuccess());
    }

    @Test
    void shouldErrorsWhenImageIsNotPresentEditedImages() {
        // Given
        final Image image = Image.builder().build();

        given(imageRepository.findById(any(Long.class)))
                .willReturn(Optional.ofNullable(null));

        // When
        Image result = imageService.edit(image);

        // Then
        //		assertNull(result.getObject());
        //		assertThat(result.getMessage(), equalTo("Image with id:" + image.getId() + " is not found"));
        //		assertFalse(result.isSuccess());
    }

    @Test
    void shouldDeleteImageById() {
        // Given
        final Long justRandomId = 5L;

        // When
        imageService.deleteById(justRandomId);

        // Then
        //		assertNull(result.getObject());
        //		assertThat(result.getMessage(), equalTo("Succesfully deleted an image"));
        //		assertTrue(result.isSuccess());
    }

    @Test
    void shouldThrowExceptionWhenDeletingImage() {
        // Given
        final Long justRandomId = 5L;

        doThrow(IllegalArgumentException.class)
                .when(imageRepository).deleteById(any(Long.class));

        // When
        imageService.deleteById(justRandomId);

        // Then
        //		assertNull(result.getObject());
        //		assertThat(result.getMessage(), equalTo("Image can not be deleted"));
        //		assertFalse(result.isSuccess());
    }

    @Test
    void shouldReturnImageFoundById() {
        // Given
        final Image imageReturned = Image.builder().build();
        final Long justRandomId = 565L;

        given(imageRepository
                .findById(any(Long.class)))
                .willReturn(Optional.ofNullable(imageReturned));

        // When
        final Image result = imageService.findById(justRandomId);

        // Then

    }

    @Test
    void shouldReturnErrorWhenNoImageFoundById() {
        // Given
        final Image imageReturned = Image.builder().build();
        final Long justRandomId = 565L;

        given(imageRepository
                .findById(any(Long.class)))
                .willReturn(Optional.ofNullable(null));

        // When
        final Image result = imageService.findById(justRandomId);

        // Then
        //		assertNull(result.getObject());
        //		assertThat(result.getMessage(), equalTo("Image with id:" + justRandomId + " is not found"));
        //		assertFalse(result.isSuccess());
    }


}
