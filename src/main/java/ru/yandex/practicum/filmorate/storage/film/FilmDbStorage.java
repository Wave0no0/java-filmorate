package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

import java.util.Optional;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> getFilm(int id) {
        // SQL-запрос для получения фильма
        String sqlFilm = """
        SELECT f.*, r.name AS rating_name
        FROM films f
        LEFT JOIN ratings r ON f.mpa_rating = r.id
        WHERE f.id = ?
    """;

        // Извлекаем основную информацию о фильме
        Film film = jdbcTemplate.query(sqlFilm, rs -> {
            if (rs.next()) {
                // Используем доступный конструктор без списка жанров
                Film f = new Film(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration")
                );

                // Устанавливаем рейтинг (MPA), если он есть
                if (rs.getInt("mpa_rating") != 0) {
                    f.setMpa(new Rating(rs.getInt("mpa_rating"), rs.getString("rating_name")));
                }
                return f;
            }
            return null;
        }, id);

        if (film == null) {
            return Optional.empty();
        }

        // SQL-запрос для получения жанров фильма
        String sqlGenres = """
        SELECT g.id, g.name
        FROM film_genres fg
        JOIN genres g ON fg.genre_id = g.id
        WHERE fg.film_id = ?
    """;

        // Извлекаем жанры и добавляем их в фильм
        List<Genre> genres = jdbcTemplate.query(sqlGenres, (rs, rowNum) ->
                new Genre(rs.getInt("id"), rs.getString("name")), id);

        film.setGenres(genres);

        // Возвращаем фильм
        return Optional.of(film);
    }

    @Override
    public Film addFilm(Film film) {
        // SQL для добавления фильма в таблицу "films"
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Выполняем вставку данных о фильме и получаем сгенерированный ID
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(filmId);

        // Сохраняем жанры фильма в таблице "film_genres"
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", filmId, genre.getId());
            }
        }

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
        log.info("Получение всех фильмов из базы данных");
        String sql = """
        SELECT f.*, r.name AS rating_name, fg.genre_id, g.name AS genre_name
        FROM films f
        LEFT JOIN ratings r ON f.mpa_rating = r.id
        LEFT JOIN film_genres fg ON f.id = fg.film_id
        LEFT JOIN genres g ON fg.genre_id = g.id
    """;

        List<Film> films = jdbcTemplate.query(sql, new FilmWithGenresExtractor());
        log.info("Получено фильмов: {}", films.size());
        return films;
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
