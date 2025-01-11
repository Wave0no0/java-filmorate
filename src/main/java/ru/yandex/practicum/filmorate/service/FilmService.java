package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.PriorityQueue;

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
        Film film = validateFilmAndUserExistence(filmId, userId);
        film.addLike(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = validateFilmAndUserExistence(filmId, userId);
        film.removeLike(userId);
    }

    public List<Film> getPopularFilms(int count) {
        PriorityQueue<Film> topFilms = new PriorityQueue<>((f1, f2) -> Integer.compare(f1.getLikeCount(), f2.getLikeCount()));
        for (Film film : filmStorage.getAllFilms()) {
            topFilms.offer(film);
            if (topFilms.size() > count) {
                topFilms.poll();
            }
        }
        return topFilms.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikeCount(), f1.getLikeCount()))
                .toList();
    }

    private Film validateFilmAndUserExistence(int filmId, int userId) {
        Film film = getFilmById(filmId);
        userService.getUserById(userId);
        return film;
    }
}
