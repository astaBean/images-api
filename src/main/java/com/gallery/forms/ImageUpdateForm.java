package com.gallery.forms;

import java.util.UUID;

import javax.validation.constraints.Size;

import org.springframework.context.annotation.Primary;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageUpdateForm {

    private UUID uuid;

    @Size(max = 50, message = "Image title's maximum size should be 50 characters")
    private String title;

    @Size(max = 300, message = "Description's maximum size should be 300 characters")
    private String description;
}
