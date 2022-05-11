package com.gallery.restcontroller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gallery.domain.Image;
import com.gallery.repository.ImageRepository;
import com.gallery.tools.NotificationMessage;
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
import java.util.Optional;
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

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldReturnAllImages() throws Exception {
        // Add images
        final Image image1 = Image.builder().title("Image1").path("path1").build();
        final Image image2 = Image.builder().title("Image2").path("path2").build();
        imageRepository.save(image1);
        imageRepository.save(image2);

        final String json = mockMvc.perform(get("/image/all"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Image> result = mapper.readValue(json, new TypeReference<List<Image>>(){});
        assertThat(result, hasSize(2));
        assertThat(mapper.convertValue(result.get(0), Image.class), samePropertyValuesAs(image1));
        assertThat(mapper.convertValue(result.get(1), Image.class), samePropertyValuesAs(image2));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldCreateAnImage() throws Exception {
        String title = "TestingTitle";
        String description = "Some Description";
        MockMultipartFile file = new MockMultipartFile("file", "anyFile".getBytes());

        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders
                        .multipart("/image")
                        .file(file)
                        .header("content-type", "multipart/*")
                        .param("description", description)
                        .param("title", title));

        result.andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();
        List<Object> resultList = mapper.readValue(json, List.class);
        Image resultImage = mapper.convertValue(resultList.get(0), Image.class);
        List<NotificationMessage> notifyMessages = new ObjectMapper().convertValue(resultList.get(1), List.class);

        assertThat(notifyMessages, contains("Image updated successfully"));
        assertThat(resultImage.getTitle(), is(title));
        assertThat(resultImage.getDescription(), is(description));

        final Optional<Image> imageInDb = imageRepository.findById(resultImage.getUuid());

        assertThat(resultImage, samePropertyValuesAs(imageInDb));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnErrorsWhenTitleAndDescriptionAreTooLong() throws Exception {
        String title = "AAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaav";
        String description = "AAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaavAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaa";

        MockMultipartFile file = new MockMultipartFile("file", "anyFile".getBytes());

        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders
                        .multipart("/image")
                        .file(file)
                        .header("content-type", "multipart/*")
                        .param("description", description)
                        .param("title", title));

        result.andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();
        List<Object> resultList = mapper.readValue(json, List.class);
        String responseResult = mapper.convertValue(resultList.get(0), String.class);
        List<NotificationMessage> notifyMessages = new ObjectMapper().convertValue(resultList.get(1), List.class);

        assertThat(responseResult, isEmptyOrNullString());
        assertThat(notifyMessages.size(), is(2));
        assertThat(notifyMessages, contains("Image title's maximum size should be 50 characters"));
        assertThat(notifyMessages, contains("Description's maximum size should be 300 characters"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnEditedImage() throws Exception {
        Image imageSaved = imageRepository.save(Image.builder().path("anything").build());
        MockMultipartFile file = new MockMultipartFile("file", "anyFile".getBytes());
        String description = "New";
        String title = "New Title";

        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders
                        .multipart("/image/createOrUpdate")
                        .file(file)
                        .header("content-type", "multipart/*")
                        .param("description", description)
                        .param("title", title)
                        .param("uuid", String.valueOf(imageSaved.getUuid())));

        result.andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();

        List<Object> resultList = mapper.readValue(json, List.class);

        Image resultImage = mapper.convertValue(resultList.get(0), Image.class);
        List<NotificationMessage> notifyMessages = mapper.convertValue(resultList.get(1), List.class);

        assertThat(resultImage, not(imageSaved));
        assertThat(resultImage.getDescription(), is(description));
        assertThat(resultImage.getTitle(), is(title));
        assertThat(resultImage.getUuid(), is(imageSaved.getUuid()));
        assertThat(notifyMessages.get(0), is("Image updated successfully"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnAnImage() throws Exception {
        Image imageSaved = imageRepository.save(Image.builder().path("anything").build());

        ResultActions result = mockMvc.perform(get("/image/" + imageSaved.getUuid()));

        result.andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();

        List<Object> resultList = mapper.readValue(json, List.class);

        Image resultImage = mapper.convertValue(resultList.get(0), Image.class);
        List<NotificationMessage> notifyMessages = mapper.convertValue(resultList.get(1), List.class);

        assertThat(resultImage, samePropertyValuesAs(imageSaved));
        assertThat(notifyMessages, is(nullValue()));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnErrorThatImageIsNotFound() throws Exception {
        final long imageId = 99L;

        final ResultActions result = mockMvc.perform(get("/image/" + imageId));

        result.andExpect(status().isOk());
        String json = result.andReturn().getResponse().getContentAsString();
        List<Object> resultList = mapper.readValue(json, List.class);
        String responseResult = mapper.convertValue(resultList.get(0), String.class);
        List<NotificationMessage> notifyMessages = mapper.convertValue(resultList.get(1), List.class);

        assertThat(responseResult, isEmptyString());
        assertThat(notifyMessages.size(), is(1));
        assertThat(notifyMessages.get(0), is("Image with id:" + imageId + " is not found"));
    }


    @SuppressWarnings("unchecked")
    @Test
    void shouldDeleteAnImage() throws Exception {
        final UUID imageUuid = UUID.randomUUID();
        final Image imageSaved = imageRepository.save(Image.builder().uuid(imageUuid).path("anything").build());

        final String result = mockMvc.perform(get("/image/delete/" + imageSaved.getUuid()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final List<Object> resultList = mapper.readValue(result, List.class);
        final String responseResult = mapper.convertValue(resultList.get(0), String.class);
        final List<NotificationMessage> notifyMessages = mapper.convertValue(resultList.get(1), List.class);

        assertThat(responseResult, isEmptyOrNullString());
        assertThat(notifyMessages.get(0), is("Successfully deleted an image"));
        assertThat(imageRepository.findById(imageUuid).isPresent(), is(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnErrorWhenDeletingNonExistingImage() throws Exception {
        final UUID imageUuid = UUID.randomUUID();

        final String json = mockMvc.perform(get("/image/delete/" + imageUuid))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Object> resultList = mapper.readValue(json, List.class);
        String responseResult = mapper.convertValue(resultList.get(0), String.class);
        List<NotificationMessage> notifyMessages = mapper.convertValue(resultList.get(1), List.class);

        assertThat(responseResult, isEmptyOrNullString());
        assertThat(notifyMessages.get(0), is("Image can not be deleted"));
    }
}
