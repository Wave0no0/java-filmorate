package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.mpa.RatingStorage;

import java.util.List;

@Service
public class RatingService {

    private final RatingStorage ratingStorage;

    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public List<Rating> getAllRatings() {
        return ratingStorage.getAllRatings();
    }

    public Rating getRatingById(int id) {
        return ratingStorage.getRatingById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Рейтинг с ID " + id + " не найден."));
    }
}