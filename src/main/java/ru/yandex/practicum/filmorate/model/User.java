package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
