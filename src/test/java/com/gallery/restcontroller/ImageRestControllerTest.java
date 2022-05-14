package com.gallery.restcontroller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gallery.domain.Image;
import com.gallery.error.ApiErrorModel;
import com.gallery.repository.ImageRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    void testReturnAll_shouldReturnAllImages() throws Exception {
        // Add images
        Image image1 = Image.builder().title("Image1").path("path1").build();
        Image image2 = Image.builder().title("Image2").path("path2").build();
        imageRepository.save(image1);
        imageRepository.save(image2);

        String result = mockMvc.perform(get("/image/all"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Image> listOfImagesReturned = mapper.readValue(result, new TypeReference<List<Image>>() {
        });
        assertThat(listOfImagesReturned, hasSize(2));
        assertThat(listOfImagesReturned.get(0), samePropertyValuesAs(image1));
        assertThat(listOfImagesReturned.get(1), samePropertyValuesAs(image2));
    }

    @Test
    void shouldCreateAnImage() throws Exception {
        String title = "TestingTitle";
        String description = "Some Description";
        MockMultipartFile file = new MockMultipartFile("file", "anyFile".getBytes());

        String result = mockMvc
                .perform(MockMvcRequestBuilders
                        .multipart("/image")
                        .file(file)
                        .header("content-type", "multipart/*")
                        .param("description", description)
                        .param("title", title))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Image imageReturned = mapper.readValue(result, Image.class);
        assertThat(imageReturned.getTitle(), is(title));
        assertThat(imageReturned.getDescription(), is(description));

        Optional<Image> imageInDb = imageRepository.findById(imageReturned.getUuid());
        assertThat(imageInDb.isPresent(), equalTo(true));
        assertThat(imageReturned, samePropertyValuesAs(imageInDb.get()));
    }

    @Test
    void shouldReturnErrorsWhenTitleAndDescriptionAreTooLong() throws Exception {
        String title = "AAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaav";
        String description = "AAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaavAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaaAAAAAaaaaaaaaaaaa";

        MockMultipartFile file = new MockMultipartFile("file", "anyFile".getBytes());

        String result = mockMvc
                .perform(MockMvcRequestBuilders
                        .multipart("/image")
                        .file(file)
                        .header("content-type", "multipart/*")
                        .param("description", description)
                        .param("title", title))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        List<ApiErrorModel> errorsReturned = mapper.readValue(result, new TypeReference<List<ApiErrorModel>>() {
        });
        assertThat(errorsReturned, iterableWithSize(2));
        errorsReturned.forEach(error -> {
            String message = error.getMessage();
            assertThat(message, isOneOf("Description's maximum size should be 300 characters",
                    "Image title's maximum size should be 50 characters"));
        });
    }

    @Test
    void shouldReturnEditedImage() throws Exception {
        Image imageSaved = imageRepository.save(Image.builder().path("anything")
                .description("Old description")
                .title("Old title")
                .build());
        MockMultipartFile file = new MockMultipartFile("file", "anyFile".getBytes());
        String newDescription = "New";
        String newTitle = "New Title";

        String result = mockMvc
                .perform(MockMvcRequestBuilders
                        .multipart("/image")
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header("content-type", "multipart/*")
                        .param("description", newDescription)
                        .param("title", newTitle)
                        .param("uuid", imageSaved.getUuid().toString()))

                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Image imageReturned = mapper.readValue(result, Image.class);

        assertThat(imageReturned.getDescription(), is(newDescription));
        assertThat(imageReturned.getTitle(), is(newTitle));
        assertThat(imageReturned.getUuid(), is(imageSaved.getUuid()));
    }

    @Test
    void shouldReturnAnImage() throws Exception {
        Image imageSaved = imageRepository.save(Image.builder().path("anything").build());

        String result = mockMvc
                .perform(get("/image/" + imageSaved.getUuid()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Image imageReturned = mapper.readValue(result, Image.class);
        assertThat(imageReturned, samePropertyValuesAs(imageSaved));
    }

    @Test
    void shouldReturnErrorThatImageIsNotFound() throws Exception {
        UUID imageUuid = UUID.randomUUID();

        String result = mockMvc
                .perform(get("/image/" + imageUuid))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(result, isEmptyString());
    }

    @Test
    void shouldDeleteAnImage() throws Exception {
        Image imageSaved = imageRepository.save(Image.builder().path("anything").build());
        Optional<Image> imageInDb = imageRepository.findById(imageSaved.getUuid());
        assertThat("Should be saved successfully in database", imageInDb.isPresent(), equalTo(true));

        mockMvc.perform(delete("/image/" + imageSaved.getUuid()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        imageInDb = imageRepository.findById(imageSaved.getUuid());
        assertThat(imageInDb.isPresent(), equalTo(false));
    }

    @Test
    void shouldReturnErrorWhenDeletingNonExistingImage() throws Exception {
        UUID imageUuid = UUID.randomUUID();

        String result = mockMvc.perform(delete("/image/" + imageUuid))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ApiErrorModel errorModel = mapper.readValue(result, ApiErrorModel.class);

        assertThat(errorModel.getMessage(), equalTo("Image could not be deleted"));
    }
}
