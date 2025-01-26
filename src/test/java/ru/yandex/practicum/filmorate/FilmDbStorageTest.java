package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(FilmDbStorage.class)
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM film_genres");
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void shouldAddAndGetFilm() {
        Film film = new Film(0, "Test Film", "Test Description", LocalDate.of(2020, 5, 20), 120);

        Film savedFilm = filmDbStorage.addFilm(film);
        Optional<Film> retrievedFilm = filmDbStorage.getFilm(savedFilm.getId());

        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get().getName()).isEqualTo("Test Film");
        assertThat(retrievedFilm.get().getDescription()).isEqualTo("Test Description");
        assertThat(retrievedFilm.get().getReleaseDate()).isEqualTo(LocalDate.of(2020, 5, 20));
        assertThat(retrievedFilm.get().getDuration()).isEqualTo(120);
    }

    @Test
    void shouldUpdateFilm() {
        Film film = new Film(0, "Old Film", "Old Description", LocalDate.of(2010, 3, 10), 100);
        Film savedFilm = filmDbStorage.addFilm(film);
        Film updatedFilm = new Film(savedFilm.getId(), "Updated Film", "Updated Description", LocalDate.of(2015, 7, 15), 130);

        filmDbStorage.updateFilm(updatedFilm);
        Optional<Film> retrievedFilm = filmDbStorage.getFilm(savedFilm.getId());

        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get().getName()).isEqualTo("Updated Film");
        assertThat(retrievedFilm.get().getDescription()).isEqualTo("Updated Description");
        assertThat(retrievedFilm.get().getReleaseDate()).isEqualTo(LocalDate.of(2015, 7, 15));
        assertThat(retrievedFilm.get().getDuration()).isEqualTo(130);
    }
}