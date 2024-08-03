package org.acme.user;

import java.util.Set;
import java.util.UUID;

public class UserOutput {
    public UUID id;
    public String username;
    public String firstname;
    public String lastname;
    public String email;
    public Set<String> roles;

    public UserOutput(UserEntity user) {
        this.id = user.id;
        this.username = user.username;
        this.firstname = user.firstname;
        this.lastname = user.lastname;
        this.email = user.email;
        this.roles = user.roles;
    }
}
