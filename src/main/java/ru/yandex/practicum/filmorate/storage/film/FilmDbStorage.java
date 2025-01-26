package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

import java.util.Optional;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> getFilm(int id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration")
        ), id);
        return films.stream().findFirst();
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        return rowsAffected > 0 ? getFilm(film.getId()) : Optional.empty();
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration")
        ));
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
                GROUP BY f.id
                ORDER BY likes_count DESC
                LIMIT ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration")
        ), count);
    }
}
