package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        log.info("Adding new film: {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Updating film: {}", film);
        if (!filmStorage.getFilmById(film.getId()).isPresent()) {
            log.error("Film with ID {} not found", film.getId());
            throw new NotFoundException("Film with ID " + film.getId() + " not found");
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int id) {
        log.info("Retrieving film with ID: {}", id);
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film with ID " + id + " not found"));
    }

    public List<Film> getAllFilms() {
        log.info("Retrieving all films");
        return filmStorage.getAllFilms();
    }

    public void addLike(int filmId, int userId) {
        log.info("Adding like from user: {} to film: {}", userId, filmId);
        Film film = getFilmById(filmId);
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void removeLike(int filmId, int userId) {
        log.info("Removing like from user: {} to film: {}", userId, filmId);
        Film film = getFilmById(filmId);
        if (!film.getLikes().contains(userId)) {
            throw new ValidationException("User " + userId + " has not liked the film " + filmId);
        }
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Retrieving top {} popular films", count);
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }
}
