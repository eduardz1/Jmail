package jmail.models;

import lombok.Data;
import lombok.NonNull;

@Data
public class User {
  @NonNull private String Email;
  @NonNull private String Name;
  @NonNull private String Surname;
  private String Avatar;
}
