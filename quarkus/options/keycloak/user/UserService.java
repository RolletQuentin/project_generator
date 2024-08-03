package org.acme.user;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository userRepository;

    public Uni<List<UserOutput>> getUsers() {
        return userRepository.getUsers()
                .map(users -> users.stream().map(UserOutput::new).toList());
    }

    public Uni<UserOutput> getUserByEmail(final String email) {
        return userRepository.findByEmail(email)
                .map(UserOutput::new);
    }

    public Uni<UserOutput> getUserById(final UUID id) {
        return userRepository.getUserById(id)
                .map(UserOutput::new);
    }

    public Uni<UserOutput> isRegistered(SecurityIdentity identity, JsonWebToken jwt) {
        String username = identity.getPrincipal().getName();
        String email = jwt.getClaim("email");
        String firstName = jwt.getClaim("given_name");
        String lastName = jwt.getClaim("family_name");
        Set<String> roles = identity.getRoles();

        return getUserByEmail(email)
                .onItem()
                .ifNull()
                .switchTo(() -> userRepository.create(new UserEntity(
                        username,
                        firstName,
                        lastName,
                        email,
                        roles
                )).onItem().transform(UserOutput::new));
    }
}
