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
        film.setId(nextId++);
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film not found with id: " + film.getId());
        }
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
        Film film = getFilmById(filmId);
        likes.get(filmId).add(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        likes.get(filmId).remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> likes.get(f2.getId()).size() - likes.get(f1.getId()).size())
                .limit(count)
                .toList();
    }
}
