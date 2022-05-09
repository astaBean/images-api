package com.gallery.domain;


import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "image")
public class Image {

    public static final String DATE_FORMAT = "YYYY/MM/dd HH:mm";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 50)
    private String title;

    @Lob
    @Column(length = 300)
    private String description;

    @Column(nullable = false)
    private String path;

    @Column(name = "addedDate")
    @DateTimeFormat(pattern = DATE_FORMAT)
    @Builder.Default
    private LocalDateTime dateAdded = LocalDateTime.now();

    @Basic(optional = false)
    @Column(name = "updateDate",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @DateTimeFormat(pattern = DATE_FORMAT)
    @Builder.Default
    private LocalDateTime dateUpdated = LocalDateTime.now();

}
