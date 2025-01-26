package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class); // Логгер
    private final FilmService filmService;

    // Получение всех фильмов
    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Fetching all films");
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    // Получение фильма по ID
    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable int id) {
        log.info("Fetching film with ID: {}", id);

        if (id <= 0) {
            log.error("Invalid Film ID: {}", id);
            throw new IllegalArgumentException("Film ID must be greater than 0.");
        }

        Film film = filmService.getFilmById(id); // ResourceNotFoundException выбрасывается в FilmService, если фильм не найден
        log.info("Film found: {}", film);

        return ResponseEntity.ok(film);
    }

    // Создание нового фильма
    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        log.info("Creating film: {}", film);
        Film createdFilm = filmService.createFilm(film);
        log.info("Film created: {}", createdFilm);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    // Обновление фильма
    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Updating film: {}", film);
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Film updated: {}", updatedFilm);
        return ResponseEntity.ok(updatedFilm);
    }

    // Добавление лайка фильму
    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Adding like to film ID: {} by user ID: {}", id, userId);
        filmService.addLike(id, userId);
        log.info("Like added successfully");
        return ResponseEntity.ok(Map.of("message", "Like added successfully"));
    }

    // Удаление лайка у фильма
    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Removing like from film ID: {} by user ID: {}", id, userId);
        filmService.removeLike(id, userId);
        log.info("Like removed successfully");
        return ResponseEntity.ok(Map.of("message", "Like removed successfully"));
    }

    // Получение популярных фильмов
    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Fetching top {} popular films", count);
        return ResponseEntity.ok(filmService.getPopularFilms(count));
    }
}