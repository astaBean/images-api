package com.gallery.restcontroller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gallery.domain.Image;
import com.gallery.repository.ImageRepository;
import com.gallery.tools.NotificationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ImageRestControllerTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MAPPER.registerModule(new JavaTimeModule());
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnAllImages() throws Exception {
        // Given
        Image image1 = Image.builder().title("Image1").path("path1").build();
        Image image2 = Image.builder().title("Image2").path("path2").build();
        imageRepository.save(image1);
        imageRepository.save(image2);

        // When
        ResultActions result = mockMvc.perform(get("/image/all"));

        // Then
        result.andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();
        List<Object> resultList = MAPPER.readValue(json, List.class);
        List<Image> images = MAPPER.convertValue(resultList.get(0), List.class);
        List<NotificationMessage> notifyMessages = new ObjectMapper().convertValue(resultList.get(1), List.class);

        assertThat(images, hasSize(2));
        assertThat(MAPPER.convertValue(images.get(0), Image.class), samePropertyValuesAs(image1));
        assertThat(MAPPER.convertValue(images.get(1), Image.class), samePropertyValuesAs(image2));
    }

    @Test
    void shouldCreateAnImage() throws Exception {
        // Given
        String title = "TestingTitle";
        String description = "Some Description";
        MockMultipartFile file = new MockMultipartFile("file", "anyFile".getBytes());

        // When
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders
                        .multipart("/image")
                        .file(file)
                        .header("content-type", "multipart/*")
                        .param("description", description)
                        .param("title", title));

        // Then
        result.andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();
        List<Object> resultList = MAPPER.readValue(json, List.class);
        Image resultImage = MAPPER.convertValue(resultList.get(0), Image.class);
        List<NotificationMessage> notifyMessages = new ObjectMapper().convertValue(resultList.get(1), List.class);

        assertThat(notifyMessages.contains("Image updated successfully"), is(true));
        assertThat(resultImage.getTitle(), is(title));
        assertThat(resultImage.getDescription(), is(description));
        assertThat(resultImage, samePropertyValuesAs(imageRepository.findById(resultImage.getUuid()).get()));
    }

    @Test
    void shouldReturnErrorsWhenTitleAndDescriptionAreTooLong() throws Exception {
        // Given
        String title = "AAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaav";
        String description = "AAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaavAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaa";

        MockMultipartFile file = new MockMultipartFile("file", "anyFile".getBytes());

        // When
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders
                        .multipart("/image")
                        .file(file)
                        .header("content-type", "multipart/*")
                        .param("description", description)
                        .param("title", title));

        // Then
        result.andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();
        List<Object> resultList = MAPPER.readValue(json, List.class);
        String responseResult = MAPPER.convertValue(resultList.get(0), String.class);
        List<NotificationMessage> notifyMessages = new ObjectMapper().convertValue(resultList.get(1), List.class);

        assertThat(responseResult, isEmptyOrNullString());
        assertThat(notifyMessages.size(), is(2));
        assertThat(notifyMessages.contains("Image title's maximum size should be 50 characters"), is(true));
        assertThat(notifyMessages.contains("Description's maximum size should be 300 characters"), is(true));
    }

    @Test
    void shouldReturnEditedImage() throws Exception {
        // Given
        Image imageSaved = imageRepository.save(Image.builder().path("anything").build());
        MockMultipartFile file = new MockMultipartFile("file", "anyFile".getBytes());
        String description = "New";
        String title = "New Title";

        // When
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders
                        .multipart("/image/createOrUpdate")
                        .file(file)
                        .header("content-type", "multipart/*")
                        .param("description", description)
                        .param("title", title)
                        .param("uuid", String.valueOf(imageSaved.getUuid())));

        // Then
        result.andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();

        List<Object> resultList = MAPPER.readValue(json, List.class);

        Image resultImage = MAPPER.convertValue(resultList.get(0), Image.class);
        List<NotificationMessage> notifyMessages = MAPPER.convertValue(resultList.get(1), List.class);

        assertThat(resultImage, not(imageSaved));
        assertThat(resultImage.getDescription(), is(description));
        assertThat(resultImage.getTitle(), is(title));
        assertThat(resultImage.getUuid(), is(imageSaved.getUuid()));
        assertThat(notifyMessages.get(0), is("Image updated successfully"));
    }

    @Test
    void shouldReturnAnImage() throws Exception {
        // Given
        Image imageSaved = imageRepository.save(Image.builder().path("anything").build());

        // When
        ResultActions result = mockMvc.perform(get("/image/" + imageSaved.getUuid()));

        // Then
        result.andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();

        List<Object> resultList = MAPPER.readValue(json, List.class);

        Image resultImage = MAPPER.convertValue(resultList.get(0), Image.class);
        List<NotificationMessage> notifyMessages = MAPPER.convertValue(resultList.get(1), List.class);

        assertThat(resultImage, samePropertyValuesAs(imageSaved));
        assertThat(notifyMessages, is(nullValue()));
    }

    @Test
    void shouldReturnErrorThatImageIsNotFound() throws Exception {
        // Given
        Long imageId = 99L;

        // When
        ResultActions result = mockMvc.perform(get("/image/" + imageId.toString()));

        // Then
        result.andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();
        List<Object> resultList = MAPPER.readValue(json, List.class);
        String responseResult = MAPPER.convertValue(resultList.get(0), String.class);
        List<NotificationMessage> notifyMessages = MAPPER.convertValue(resultList.get(1), List.class);

        assertThat(responseResult, isEmptyString());
        assertThat(notifyMessages.size(), is(1));
        assertThat(notifyMessages.get(0), is("Image with id:" + imageId + " is not found"));
    }


    @Test
    void shouldDeleteAnImage() throws Exception {
        // Given
        final UUID imageUuid = UUID.randomUUID();
        final Image imageSaved = imageRepository.save(Image.builder().uuid(imageUuid).path("anything").build());

        // When
        final String result = mockMvc.perform(get("/image/delete/" + imageSaved.getUuid()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Then
        final List<Object> resultList = MAPPER.readValue(result, List.class);
        final String responseResult = MAPPER.convertValue(resultList.get(0), String.class);
        final List<NotificationMessage> notifyMessages = MAPPER.convertValue(resultList.get(1), List.class);

        assertThat(responseResult, isEmptyOrNullString());
        assertThat(notifyMessages.get(0), is("Successfully deleted an image"));
        assertThat(imageRepository.findById(imageUuid).isPresent(), is(false));
    }

    @Test
    void shouldReturnErrorWhenDeletingNonExistingImage() throws Exception {
        // Given
        final UUID imageUuid = UUID.randomUUID();

        // When
        final String json = mockMvc.perform(get("/image/delete/" + imageUuid))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Then
        List<Object> resultList = MAPPER.readValue(json, List.class);
        String responseResult = MAPPER.convertValue(resultList.get(0), String.class);
        List<NotificationMessage> notifyMessages = MAPPER.convertValue(resultList.get(1), List.class);

        assertThat(responseResult, isEmptyOrNullString());
        assertThat(notifyMessages.get(0), is("Image can not be deleted"));
    }
}