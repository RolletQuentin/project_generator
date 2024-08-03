package org.acme.user;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {
    public Uni<List<UserEntity>> getUsers() {
        return this.listAll();
    }

    public Uni<UserEntity> getUserById(final UUID id) {
        PanacheQuery<UserEntity> query = this.find("id", id);
        return query.firstResult();
    }
    public Uni<UserEntity> findByEmail(final String email) {
        PanacheQuery<UserEntity> query = this.find("email", email);
        return query.firstResult();
    }

    public Uni<UserEntity> create(final UserEntity user) {
        return Panache.withTransaction(() -> this.persist(user));
    }
}
