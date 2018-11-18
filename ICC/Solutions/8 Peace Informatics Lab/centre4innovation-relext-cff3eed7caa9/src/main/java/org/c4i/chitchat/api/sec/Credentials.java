package org.c4i.chitchat.api.sec;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.c4i.util.Hash;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * User-password combination.
 * See: Salted Password Hashing - Doing it Right, https://crackstation.net/hashing-security.htm
 * @author Arvid Halma
 * @version 16-9-2016 - 18:53
 */
public class Credentials {
    @NotEmpty
    @JsonProperty
    private User user;

    @NotEmpty
    @JsonProperty
    private String hashedPassword;

    @NotEmpty
    @JsonProperty
    private String salt;

    public Credentials() {
    }

    public Credentials(User user, String hashedPassword, String salt) {
        this.user = user;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public User getUser() {
        return user;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public boolean match(String plainPassword){
        if(hashedPassword == null || plainPassword == null)
            return false;
        return hashedPassword.equals(Hash.sha256Hex(salt + plainPassword));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credentials)) return false;

        Credentials that = (Credentials) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (hashedPassword != null ? !hashedPassword.equals(that.hashedPassword) : that.hashedPassword != null)
            return false;
        return salt != null ? salt.equals(that.salt) : that.salt == null;

    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (hashedPassword != null ? hashedPassword.hashCode() : 0);
        result = 31 * result + (salt != null ? salt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "user=" + user +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
