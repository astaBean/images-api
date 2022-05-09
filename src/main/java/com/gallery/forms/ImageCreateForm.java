package com.gallery.forms;

import javax.validation.constraints.Size;

public class ImageCreateForm {

    private Long id;

    @Size(max = 50, message = "Image title's maximum size should be 50 characters")
    private String title;

    @Size(max = 300, message = "Description's maximum size should be 300 characters")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
