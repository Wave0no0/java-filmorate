package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;

    @Email(message = "Email must be valid.")
    @NotBlank(message = "Email cannot be blank.")
    private String email;

    @NotBlank(message = "Login cannot be blank.")
    @Pattern(regexp = "\\S+", message = "Login must not contain spaces.")
    private String login;

    private String name;

    @PastOrPresent(message = "Birthday must be in the past or present.")
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();

    public void validate() {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email must contain '@'.");
        }
        if (login == null || login.isBlank() || login.contains(" ")) {
            throw new IllegalArgumentException("Login cannot be blank or contain spaces.");
        }
        if (name == null || name.isBlank()) {
            name = login;
        }
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthday must not be in the future.");
        }
    }

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public void removeFriend(int friendId) {
        friends.remove(friendId);
    }

    public Set<Integer> getFriends() {
        return new HashSet<>(friends);
    }
}
