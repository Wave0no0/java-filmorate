package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingDbStorage implements ru.yandex.practicum.filmorate.storage.mpa.RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> getAllRatings() {
        String sql = "SELECT * FROM ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Rating(rs.getInt("id"), rs.getString("name"))
        );
    }

    @Override
    public Optional<Rating> getRatingById(int id) {
        String sql = "SELECT * FROM ratings WHERE id = ?";
        return jdbcTemplate.query(sql, (rs) -> {
            if (rs.next()) {
                return Optional.of(new Rating(rs.getInt("id"), rs.getString("name")));
            }
            return Optional.empty();
        }, id);
    }

    public Rating findRatingOrThrow(int id) {
        return getRatingById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Рейтинг с ID " + id + " не найден."));
    }
}