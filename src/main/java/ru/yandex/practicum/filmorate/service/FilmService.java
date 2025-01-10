package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Service
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film createFilm(Film film) {
        int newId = generateId();
        Film newFilm = film.toBuilder().id(newId).build();
        films.put(newId, newFilm);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new IllegalArgumentException("Film with ID " + film.getId() + " not found.");
        }
        films.put(film.getId(), film);
        return film;
    }

    public Film getFilmById(int id) {
        // Обрабатываем отсутствие фильма через Optional
        return Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Film with ID " + id + " not found."));
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }

    private int generateId() {
        return id++;
    }
}
