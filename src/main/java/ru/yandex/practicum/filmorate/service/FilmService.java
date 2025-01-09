package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addLike(int filmId, int userId) {
        Film film = getFilmOrThrow(filmId);
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        Film film = getFilmOrThrow(filmId);
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getMostPopular(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private Film getFilmOrThrow(int id) {
        return filmStorage.getFilmById(id).orElseThrow(() -> new IllegalArgumentException("Film not found: " + id));
    }
}
