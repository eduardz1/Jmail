package jmail.lib.models;

import java.io.IOException;

import jmail.lib.helpers.JsonHelper;
import jmail.lib.helpers.SystemIOHelper;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
public class User {
    @NonNull
    private String email;
    @NonNull
    private String name;
    @NonNull
    private String surname;
    @NonNull
    private String passwordSHA256;
    private String avatar; // PATH

    // Default constructor for Jackson
    public User() {
    }
}
