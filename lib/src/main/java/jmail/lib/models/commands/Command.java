package jmail.lib.models.commands;

import lombok.Data;
import lombok.NonNull;

@Data
public class Command {
// facciamolo così https://dzone.com/articles/command-design-pattern-in-java :)
    @NonNull private String action;
}
