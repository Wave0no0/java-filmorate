package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable int id) {
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        Film createdFilm = filmService.createFilm(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        Film updatedFilm = filmService.updateFilm(film);
        return ResponseEntity.ok(updatedFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        return ResponseEntity.ok(Map.of("message", "Like added successfully"));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
        return ResponseEntity.ok(Map.of("message", "Like removed successfully"));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(filmService.getPopularFilms(count));
    }
}
