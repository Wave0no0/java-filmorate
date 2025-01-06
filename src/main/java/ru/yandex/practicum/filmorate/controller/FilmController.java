package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Импортируем для логирования
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j // Аннотация для логирования
public class FilmController {
    private final List<Film> films = new ArrayList<>();

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            films.add(film);
            log.info("Фильм успешно добавлен: {}", film); // Логируем успешное добавление
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка при добавлении фильма: {}", e.getMessage()); // Логируем ошибку валидации
            throw e;
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            films.set(film.getId(), film); // Пример: замена по индексу id
            log.info("Фильм успешно обновлен: {}", film); // Логируем успешное обновление
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка при обновлении фильма: {}", e.getMessage()); // Логируем ошибку валидации
            throw e;
        }
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return films;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Ошибка валидации: Название фильма не может быть пустым"); // Логируем ошибку валидации
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Ошибка валидации: Описание фильма не может превышать 200 символов"); // Логируем ошибку валидации
            throw new ValidationException("Описание фильма не может превышать 200 символов.");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка валидации: Дата релиза не может быть раньше 28 декабря 1895 года"); // Логируем ошибку валидации
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            log.error("Ошибка валидации: Продолжительность фильма должна быть положительным числом"); // Логируем ошибку валидации
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }
}
