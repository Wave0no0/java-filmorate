package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Film name cannot be blank.")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters.")
    private String description;

    @NotNull(message = "Release date cannot be null.")
    @Past(message = "Release date must be in the past.")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be positive.")
    private int duration;

    private final Set<Integer> likes = new HashSet<>();

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Film name cannot be blank.");
        }
        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("Description cannot exceed 200 characters.");
        }
        if (releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new IllegalArgumentException("Release date must be after December 28, 1895.");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive.");
        }
    }

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void removeLike(int userId) {
        likes.remove(userId);
    }

    public int getLikeCount() {
        return likes.size();
    }
}
