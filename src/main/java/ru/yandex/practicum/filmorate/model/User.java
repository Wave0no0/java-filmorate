package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.filmorate.validation.NoSpaces;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private int id;

    @Email(message = "Invalid email format.")
    @NotBlank(message = "Email cannot be empty.")
    private String email;

    @NoSpaces
    @NotBlank(message = "Login cannot be empty.")
    private String login;

    private String name;

    @PastOrPresent(message = "Birthday must be in the past or present.")
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();

    public String getName() {
        return (name == null || name.isBlank()) ? login : name;
    }
}
