package org.corpauration.s3;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class S3Repository implements PanacheRepository<FileEntity> {

    public Uni<FileEntity> create(final FileEntity fileEntity) {
        return Panache.withTransaction(() -> this.persist(fileEntity));
    }

    public Uni<List<FileEntity>> getFiles() {
        return this.listAll();
    }

    public Uni<List<FileEntity>> getEventFiles(UUID eventId) {
        return this.list("eventId", eventId);
    }

    public Uni<FileEntity> getFileEntityById(String objectKey) {
        return this.find("objectKey", objectKey).firstResult();
    }
}
