package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import ru.yandex.practicum.filmorate.validation.ReleaseDateConstraint;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {
    private int id;

    @NotBlank(message = "Film name cannot be empty")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @NotNull(message = "Release date cannot be null")
    @ReleaseDateConstraint(message = "Release date cannot be earlier than December 28, 1895")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be positive")
    private int duration;

    private final Set<Integer> likes = new HashSet<>();
}
