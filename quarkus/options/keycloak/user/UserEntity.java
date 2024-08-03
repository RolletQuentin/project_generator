package org.acme.user;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.util.Set;
import java.util.UUID;

@Entity
public class UserEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue
    @Schema(name= "id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
    public UUID id;

    public String username;
    public String firstname;
    public String lastname;
    public String email;
    @ElementCollection(fetch = FetchType.EAGER)
    public Set<String> roles;

    public UserEntity() {}

    public UserEntity(
            String username,
            String firstname,
            String lastname,
            String email,
            Set<String> roles
    ) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.roles = roles;
        this.username = username;
    }
}
