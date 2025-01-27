package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.mpa.RatingDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(RatingDbStorage.class)
class RatingDbStorageTest {

    @Autowired
    private RatingDbStorage ratingDbStorage;

    @Test
    void shouldGetAllRatings() {
        List<Rating> ratings = ratingDbStorage.getAllRatings();

        assertThat(ratings).isNotEmpty();
        assertThat(ratings).extracting(Rating::getName).contains("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void shouldGetRatingById() {
        Optional<Rating> rating = ratingDbStorage.getRatingById(1);

        assertThat(rating).isPresent();
        assertThat(rating.get().getName()).isEqualTo("G");
    }
}