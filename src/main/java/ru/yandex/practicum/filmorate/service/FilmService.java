package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilm(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film with ID " + id + " not found."));
    }

    public Film createFilm(Film film) {
        film.validate();
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        film.validate();
        return filmStorage.updateFilm(film)
                .orElseThrow(() -> new ResourceNotFoundException("Film with ID " + film.getId() + " not found."));
    }

    public void addLike(int filmId, int userId) {
        Film film = getValidFilm(filmId);
        User user = userService.getValidUser(userId);
        film.addLike(user.getId());
    }

    public void removeLike(int filmId, int userId) {
        Film film = getValidFilm(filmId);
        User user = userService.getValidUser(userId);
        film.removeLike(user.getId());
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikeCount(), f1.getLikeCount()))
                .limit(count)
                .toList();
    }

    private Film getValidFilm(int filmId) {
        return getFilmById(filmId);
    }
}
