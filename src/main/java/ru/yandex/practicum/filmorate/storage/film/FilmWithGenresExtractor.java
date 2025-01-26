package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FilmWithGenresExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Film> films = new HashMap<>();

        while (rs.next()) {
            int filmId = rs.getInt("id");
            Film film = films.get(filmId);

            // Если фильм ещё не обработан, создаём его
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));

                // Добавляем объект MPA
                Mpa mpa = new Mpa(rs.getInt("mpa_rating_id"), rs.getString("mpa_rating"));
                film.setMpa(mpa);

                // Инициализируем список жанров с сортировкой по ID
                film.setGenres(new TreeSet<>(Comparator.comparingInt(Genre::getId)));

                films.put(filmId, film);
            }

            // Добавляем жанры, если они существуют
            int genreId = rs.getInt("genre_id");
            if (genreId > 0) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre);
            }
        }

        return new ArrayList<>(films.values());
    }
}