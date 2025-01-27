package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    @JsonProperty("id")
    private int id;

    @JsonProperty("email")
    @NotBlank(message = "Email не может быть пустым")
    @Pattern(regexp = ".+@.+\\..+", message = "Email должен быть корректным")
    private String email;

    @JsonProperty("login")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    @JsonProperty("name")
    private String name;

    @JsonProperty("birthday")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;

    private Set<Integer> friends = new HashSet<>();

    public String getName() {
        return (name == null || name.isBlank()) ? login : name;
    }

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public void removeFriend(int friendId) {
        friends.remove(friendId);
    }
}