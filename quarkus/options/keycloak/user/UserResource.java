package org.acme.user;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.UUID;

@Path("users")
@ApplicationScoped
public class UserResource {
    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @Inject
    SecurityIdentity identity;

    @GET
    @WithSession
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<UserOutput>> getAllUsers() {
        return userService.getUsers();
    }

    @GET
    @Path("/{id}")
    @WithSession
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserOutput> getUserById(@PathParam("id") UUID id) {
        return userService.getUserById(id);
    }

    @GET
    @Path("/isRegistered")
    @WithSession
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserOutput> isRegistered() {
        return userService.isRegistered(identity, jwt);
    }
}
