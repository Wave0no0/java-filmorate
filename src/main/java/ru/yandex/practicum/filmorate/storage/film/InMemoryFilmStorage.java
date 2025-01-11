package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    @Override
    public Film addFilm(Film film) {
        film.validate();
        film.setId(currentId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            return Optional.empty();
        }
        film.validate();
        films.put(film.getId(), film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> getFilm(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
