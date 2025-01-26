package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FilmWithGenresExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Film> films = new LinkedHashMap<>();

        while (rs.next()) {
            int filmId = rs.getInt("id");
            Film film = films.computeIfAbsent(filmId, id -> {
                try {
                    return new Film(
                            id,
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getInt("duration")
                    );
                } catch (SQLException e) {
                    throw new RuntimeException("Ошибка при создании фильма", e);
                }
            });

            // Установка рейтинга
            if (rs.getInt("rating_id") != 0) {
                Rating mpa = new Rating(rs.getInt("rating_id"), rs.getString("rating_name"));
                film.setMpa(mpa);
            }

            // Установка жанров
            int genreId = rs.getInt("genre_id");
            if (!rs.wasNull()) {
                Genre genre = new Genre(genreId, rs.getString("genre_name"));
                if (!film.getGenres().contains(genre)) {
                    film.getGenres().add(genre);
                }
            }
        }

        return new ArrayList<>(films.values());
    }
}