package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@JsonPropertyOrder({"id", "name", "description", "releaseDate", "duration", "mpa", "genres"})
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

    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();

    private Set<Genre> genres = new HashSet<>();

    @NotNull(message = "MPA rating cannot be null.")
    private Mpa mpa;

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
        if (mpa == null) {
            throw new IllegalArgumentException("MPA rating cannot be null.");
        }
    }

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void removeLike(int userId) {
        likes.remove(userId);
    }

    @JsonIgnore
    public int getLikeCount() {
        return likes.size();
    }
}