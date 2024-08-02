package org.corpauration.s3;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.io.File;
import java.util.UUID;

@Entity
public class FileEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public UUID id;

    public String filename;
    public Long size;
    public UUID userId;

    public FileEntity() {}

    public FileEntity(String filename, Long size, UUID userId) {
        this.filename = filename;
        this.size = size;
        this.userId = userId;
    }

    public FileEntity(FormData formData) {
        this.filename = formData.filename;
        this.size = formData.data.length();
        this.userId = formData.userId;
    }
}
