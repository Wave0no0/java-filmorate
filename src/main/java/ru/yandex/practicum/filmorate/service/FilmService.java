package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Service
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    public Film createFilm(Film film) {
        validateFilm(film);
        if (films.values().stream().anyMatch(f -> f.getName().equalsIgnoreCase(film.getName()))) {
            throw new ValidationException("Film with the same name already exists.");
        }
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film with ID " + film.getId() + " not found.");
        }
        films.put(film.getId(), film);
        return film;
    }

    public Film getFilmById(int id) {
        return Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new NotFoundException("Film with ID " + id + " not found."));
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Film name cannot be empty.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Description cannot exceed 200 characters.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(java.time.LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Release date cannot be earlier than December 28, 1895.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Duration must be positive.");
        }
    }
}
