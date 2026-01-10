package ru.mentee.blog.domain.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private String username;
  private String email;
}