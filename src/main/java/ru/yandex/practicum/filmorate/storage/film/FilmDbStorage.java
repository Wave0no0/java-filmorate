package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmWithGenresExtractor;

import java.util.List;
import java.util.Optional;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> getFilm(int id) {
        String sql = """
                SELECT f.*, g.id AS genre_id, g.name AS genre_name
                FROM films f
                LEFT JOIN film_genres fg ON f.id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.id
                WHERE f.id = ?;
                """;
        List<Film> films = jdbcTemplate.query(sql, new FilmWithGenresExtractor(), id);
        return films.stream().findFirst();
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null, film.getId());
        return rowsAffected > 0 ? getFilm(film.getId()) : Optional.empty();
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = """
                SELECT f.*, g.id AS genre_id, g.name AS genre_name
                FROM films f
                LEFT JOIN film_genres fg ON f.id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.id;
                """;
        return jdbcTemplate.query(sql, new FilmWithGenresExtractor());
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = """
                SELECT f.*, COUNT(l.user_id) AS likes_count
                FROM films f
                LEFT JOIN likes l ON f.id = l.film_id
                LEFT JOIN film_genres fg ON f.id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.id
                GROUP BY f.id
                ORDER BY likes_count DESC
                LIMIT ?;
                """;
        return jdbcTemplate.query(sql, new FilmWithGenresExtractor(), count);
    }
}
