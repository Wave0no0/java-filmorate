package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final RatingService ratingService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, GenreService genreService, RatingService ratingService) {
        this.filmStorage = filmStorage;
        this.genreService = genreService;
        this.ratingService = ratingService;
    }

    public List<Film> getAllFilms() {
        log.info("Получение всех фильмов");

        List<Film> films = filmStorage.getAllFilms();

        films.forEach(film -> {
            // Заполняем данные о MPA
            if (film.getMpa() != null && film.getMpa().getId() != null) {
                Rating mpa = ratingService.getRatingById(film.getMpa().getId());
                if (mpa != null) {
                    film.setMpa(mpa);
                } else {
                    log.warn("Рейтинг со ID {} не найден для фильма с ID {}", film.getMpa().getId(), film.getId());
                    film.setMpa(null); // Обрабатываем отсутствующий рейтинг
                }
            }

            // Заполняем данные о жанрах
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                List<Genre> genres = film.getGenres().stream()
                        .map(genre -> genreService.getGenreById(genre.getId()))
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(Genre::getId))
                        .toList();
                film.setGenres(genres);
            } else {
                film.setGenres(new ArrayList<>());
            }
        });

        log.info("Получено фильмов: {}", films.size());
        return films;
    }

    public Film getFilmById(int id) {
        log.info("Получение фильма с ID {}", id);

        // Получаем фильм из хранилища
        Film film = filmStorage.getFilm(id)
                .orElseThrow(() -> {
                    log.warn("Фильм с ID {} не найден", id);
                    return new ResourceNotFoundException("Фильм с ID " + id + " не найден.");
                });

        // Заполняем жанры (убираем дубликаты и сортируем)
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Genre> genres = film.getGenres().stream()
                    .distinct()
                    .sorted(Comparator.comparing(Genre::getId))
                    .toList();
            film.setGenres(genres);
        } else {
            film.setGenres(new ArrayList<>()); // Пустой список вместо null
        }

        log.info("Фильм с ID {} успешно получен: {}", id, film);
        return film;
    }


    public Film createFilm(Film film) {
        log.info("Создание нового фильма: {}", film);

        // Проверяем корректность данных фильма
        validateReleaseDate(film);
        validateGenresAndRating(film); // Проверяем жанры и рейтинг

        // Сохраняем фильм в базе
        Film createdFilm = filmStorage.addFilm(film);

        // Наполняем фильм полными данными (mpa и genres)
        populateFilmData(createdFilm);

        log.info("Фильм успешно создан: {}", createdFilm);
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        log.info("Обновление фильма с ID {}", film.getId());

        // Проверяем существование фильма
        getFilmById(film.getId());

        // Проверяем корректность данных
        validateReleaseDate(film);
        validateGenresAndRating(film);

        // Обновляем фильм в базе
        Film updatedFilm = filmStorage.updateFilm(film)
                .orElseThrow(() -> {
                    log.warn("Фильм с ID {} не найден для обновления", film.getId());
                    return new ResourceNotFoundException("Фильм с ID " + film.getId() + " не найден.");
                });

        // Наполняем фильм полными данными (mpa и genres)
        populateFilmData(updatedFilm);

        log.info("Фильм успешно обновлен: {}", updatedFilm);
        return updatedFilm;
    }

    public void addLike(int filmId, int userId) {
        log.info("Добавление лайка фильму с ID {} от пользователя с ID {}", filmId, userId);

        // Проверка существования фильма и пользователя
        getFilmById(filmId);
        filmStorage.addLike(filmId, userId);
        log.info("Лайк успешно добавлен");
    }

    public void removeLike(int filmId, int userId) {
        log.info("Удаление лайка у фильма с ID {} от пользователя с ID {}", filmId, userId);
        getFilmById(filmId);
        filmStorage.removeLike(filmId, userId);
        log.info("Лайк успешно удалён");
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получение {} популярных фильмов", count);
        List<Film> films = filmStorage.getPopularFilms(count);
        films.forEach(this::populateFilmData); // Добавляем названия жанров и рейтингов
        return films;
    }

    private void validateReleaseDate(Film film) {
        LocalDate earliestDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(earliestDate)) {
            throw new IllegalArgumentException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
    }

    private void validateGenresAndRating(Film film) {
        // Проверяем MPA-рейтинг
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new BadRequestException("MPA-рейтинг обязателен для указания.");
        }

        // Проверяем жанры
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            throw new BadRequestException("Фильм должен содержать хотя бы 1 жанр.");
        }

        // Убираем дубликаты жанров
        film.setGenres(film.getGenres().stream()
                .distinct()
                .toList());

        // Проверяем, что жанров минимум 2
        if (film.getGenres().size() < 2) {
            throw new BadRequestException("Фильм должен содержать минимум 2 жанра.");
        }
    }


    private void populateFilmData(Film film) {
        // Заполняем жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.setGenres(film.getGenres().stream()
                    .map(genre -> genreService.getGenreById(genre.getId()))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Genre::getId))
                    .toList());
        } else {
            film.setGenres(new ArrayList<>()); // Пустой список вместо null
        }

        // Заполняем MPA (рейтинг)
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            Rating mpa = ratingService.getRatingById(film.getMpa().getId());
            if (mpa != null) {
                film.setMpa(mpa);
            } else {
                log.warn("Рейтинг с ID {} не найден для фильма с ID {}", film.getMpa().getId(), film.getId());
                film.setMpa(null);
            }
        }
    }
}