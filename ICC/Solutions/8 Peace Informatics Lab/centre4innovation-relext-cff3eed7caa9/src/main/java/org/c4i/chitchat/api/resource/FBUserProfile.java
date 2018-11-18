package org.c4i.chitchat.api.resource;

import com.github.messenger4j.userprofile.UserProfile;
import org.c4i.chitchat.api.model.JsonDoc;

import javax.validation.constraints.NotNull;

/**
 * Container for selected Facebook profile fields.
 * @author Boaz Manger
 */
public final class FBUserProfile {

    private final String firstName;
    private final String lastName;
    private final String locale;
    private final float timezoneOffset;
    private final UserProfile.Gender gender;


    public FBUserProfile(String firstName, String lastName,
                         String locale, float timezoneOffset, UserProfile.Gender gender) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.locale = locale;
        this.timezoneOffset = timezoneOffset;
        this.gender = gender;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String locale() {
        return locale;
    }

    public float timezoneOffset() {
        return timezoneOffset;
    }

}