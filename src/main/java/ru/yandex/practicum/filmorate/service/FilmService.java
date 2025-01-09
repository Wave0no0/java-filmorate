package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Service
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();
    private int nextId = 1;

    public Film createFilm(Film film) {
        validateFilm(film);
        film.setId(nextId++);
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film not found with id: " + film.getId());
        }
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Film not found with id: " + id);
        }
        return films.get(id);
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public void addLike(int filmId, int userId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Film not found with id: " + filmId);
        }
        likes.get(filmId).add(userId);
    }

    public void removeLike(int filmId, int userId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Film not found with id: " + filmId);
        }
        likes.get(filmId).remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> likes.get(f2.getId()).size() - likes.get(f1.getId()).size())
                .limit(count)
                .toList();
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new IllegalArgumentException("Film name cannot be empty");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new IllegalArgumentException("Film description is too long");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(java.time.LocalDate.of(1895, 12, 28))) {
            throw new IllegalArgumentException("Invalid release date");
        }
        if (film.getDuration() <= 0) {
            throw new IllegalArgumentException("Film duration must be positive");
        }
    }
}
