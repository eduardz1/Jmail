package jmail.lib.models;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import lombok.NonNull;

public record Email(
    String subject,
    String body,
    @NonNull String sender,
    @NonNull List<String> recipients,
    @NonNull Date date)
    implements Serializable {}
