package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Импортируем для логирования
import org.springframework.http.ResponseEntity;
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
            // Присваиваем ID новому фильму
            int newId = films.size() + 1; // Пример простого автоинкремента
            film.setId(newId);
            films.add(film);
            log.info("Фильм успешно добавлен: {}", film);
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка при добавлении фильма: {}", e.getMessage());
            throw e;
        }
    }


    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            // Поиск фильма по ID
            Film existingFilm = films.stream()
                    .filter(f -> f.getId() == film.getId())
                    .findFirst()
                    .orElseThrow(() -> new ValidationException("Фильм с таким ID не найден."));
            // Обновление данных фильма
            existingFilm.setName(film.getName());
            existingFilm.setDescription(film.getDescription());
            existingFilm.setReleaseDate(film.getReleaseDate());
            existingFilm.setDuration(film.getDuration());
            log.info("Фильм успешно обновлен: {}", film);
            return existingFilm;
        } catch (ValidationException e) {
            log.error("Ошибка при обновлении фильма: {}", e.getMessage());
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

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage()); // Возвращаем ошибку 400 с сообщением
    }
}
