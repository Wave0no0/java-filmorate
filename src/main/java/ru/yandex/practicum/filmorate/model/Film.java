package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Getter
@Setter
public class Film {
    private int id; // Поле для идентификатора фильма
    private String name; // Поле для названия фильма
    private String description; // Поле для описания фильма
    private LocalDate releaseDate; // Поле для даты релиза фильма
    private int duration; // Поле для продолжительности фильма
}
