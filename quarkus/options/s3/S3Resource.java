package org.corpauration.s3;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestMulti;

import java.util.*;

@Path("/s3")
@ApplicationScoped
public class S3Resource {

    @Inject
    S3Service s3Service;

    @Inject
    SecurityIdentity identity;

    @POST
    @Path("/upload")
    @RolesAllowed("user")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Uni<Response> uploadFile(FormData formData) throws Exception {
        if (formData.filename == null || formData.filename.isEmpty()) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).build());
        }

        if (formData.mimetype == null || formData.mimetype.isEmpty()) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).build());
        }

        if (formData.userId == null || formData.eventId == null) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).build());
        }

        if (formData.data == null || !formData.data.exists()) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).entity("File is missing or invalid").build());
        }

        if (!identity.getRoles().contains("admin") && formData.teamId == null) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).entity("Team ID is required").build());
        }

        return s3Service.uploadFile(formData);
    }

    @GET
    @Path("download/{objectKey}")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public RestMulti<Buffer> downloadFile(@PathParam("objectKey") String objectKey) {

        return s3Service.downloadFile(objectKey);
    }

    @GET
    @WithTransaction
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<FileEntity>> listFiles() {
        return s3Service.listFiles();
    }

    @GET
    @WithTransaction
    @Path("event/{eventId}")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<FileEntity>> listEventFiles(@PathParam("eventId") UUID eventId) {
        return s3Service.listEventFiles(eventId);
    }
}