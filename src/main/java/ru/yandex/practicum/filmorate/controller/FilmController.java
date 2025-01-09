package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Validated
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<?> addFilm(@Valid @RequestBody Film film) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(filmService.addFilm(film));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFilm(@PathVariable int id, @Valid @RequestBody Film film) {
        try {
            film.setId(id);
            Film updatedFilm = filmService.updateFilm(film);
            return ResponseEntity.ok(updatedFilm);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFilmById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(filmService.getFilmById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<?> addLike(@PathVariable int id, @PathVariable int userId) {
        try {
            filmService.addLike(id, userId);
            return ResponseEntity.ok(Map.of("message", "Like added successfully"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<?> removeLike(@PathVariable int id, @PathVariable int userId) {
        try {
            filmService.removeLike(id, userId);
            return ResponseEntity.ok(Map.of("message", "Like removed successfully"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(filmService.getPopularFilms(count));
    }
}
