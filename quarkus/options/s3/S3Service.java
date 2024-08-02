package org.corpauration.s3;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestMulti;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class S3Service {
    @ConfigProperty(name = "bucket.name")
    String bucketName;

    @Inject
    S3AsyncClient s3;

    @Inject
    S3Repository s3Repository;

    private PutObjectRequest buildPutRequest(FormData formData, FileEntity fileEntity) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileEntity.id.toString())
                .contentType(formData.mimetype)
                .build();
    }

    private GetObjectRequest buildGetRequest(String objectKey) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
    }

    private static Buffer toBuffer(ByteBuffer bytebuffer) {
        byte[] result = new byte[bytebuffer.remaining()];
        bytebuffer.get(result);
        return Buffer.buffer(result);
    }

    public Uni<Response> uploadFile(FormData formData) {
        return s3Repository.create(new FileEntity((formData)))
                .flatMap(fileEntity -> {
                    // Capture context
                    Context context = Vertx.currentContext();
                    return Uni.createFrom()
                            .completionStage(() -> s3.putObject(buildPutRequest(formData, fileEntity),
                                    AsyncRequestBody.fromFile(formData.data)))
                            .emitOn(command -> context.runOnContext(x -> command.run())) // Restore context
                            .onItem().ignore().andSwitchTo(Uni.createFrom().item(Response.ok(fileEntity).build()))
                            .onFailure().recoverWithItem(Response.serverError().build());
                });
    }

    public RestMulti<Buffer> downloadFile(String objectKey) {

        return RestMulti.fromUniResponse(Uni.createFrom()
                        .completionStage(() -> s3.getObject(buildGetRequest(objectKey),
                                AsyncResponseTransformer.toPublisher())),
                response -> Multi.createFrom().safePublisher(AdaptersToFlow.publisher((Publisher<ByteBuffer>) response))
                        .map(S3Service::toBuffer),
                response -> Map.of("Content-Disposition", List.of("attachment;filename=" + objectKey), "Content-Type",
                        List.of(response.response().contentType())));
    }

    public Uni<List<FileEntity>> listFiles() {
        return s3Repository.getFiles();
    }

    public Uni<List<FileEntity>> listEventFiles(UUID eventId) {
        return s3Repository.getEventFiles(eventId);
    }
}
