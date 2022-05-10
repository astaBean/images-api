package com.gallery.domain;


import lombok.*;

import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "image")
@EqualsAndHashCode
public class Image {

    public static final String DATE_FORMAT = "YYYY/MM/dd HH:mm";

    @Id
    @GeneratedValue
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(length=16)
    private UUID uuid;

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
