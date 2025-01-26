package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    private final UserStorage userStorage;
    private final LikeDbStorage likeDbStorage;

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
        getFilmById(filmId); // Проверка существования фильма
        if (userStorage.getUser(userId).isEmpty()) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found.");
        }
        likeDbStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        getFilmById(filmId); // Проверка существования фильма
        if (userStorage.getUser(userId).isEmpty()) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found.");
        }
        likeDbStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(
                        likeDbStorage.getLikeCount(f2.getId()),
                        likeDbStorage.getLikeCount(f1.getId())))
                .limit(count)
                .collect(Collectors.toList());
    }
}