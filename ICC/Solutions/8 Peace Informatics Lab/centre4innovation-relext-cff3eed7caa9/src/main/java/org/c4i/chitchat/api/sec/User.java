package org.c4i.chitchat.api.sec;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.Principal;
import java.util.Set;

/**
 * A user name with associated roles.
 * Following: https://github.com/dropwizard/dropwizard/blob/master/dropwizard-example/src/main/java/com/example/helloworld/core/User.java
 * @author Arvid Halma
 */
public class User implements Principal {
    @JsonProperty
    private String name;

    @JsonProperty
    private Set<String> roles;


    public User() {
    }

    public User(String name) {
        this.name = name;
        this.roles = null;
    }

    public User(String name, Set<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return name != null ? name.equals(user.name) : user.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", roles=" + roles +
                '}';
    }
}