package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    @JsonIgnore // Убираем friends из JSON
    private final Map<Integer, String> friends = new HashMap<>();

    public void validate() {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank.");
        }
        if (login == null || login.isBlank() || login.contains(" ")) {
            throw new IllegalArgumentException("Login cannot be blank or contain spaces.");
        }
        if (name == null || name.isBlank()) {
            name = login; // Если имя не указано, используем логин
        }
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthday must not be in the future.");
        }
    }

    public void addFriend(int friendId, String status) {
        friends.put(friendId, status);
    }

    public void updateFriendStatus(int friendId, String status) {
        if (friends.containsKey(friendId)) {
            friends.put(friendId, status);
        }
    }

    public void removeFriend(int friendId) {
        friends.remove(friendId);
    }

    // Возвращаем друзей в виде списка объектов
    @JsonProperty("friendsList")
    public List<Map<String, Object>> getFriendsList() {
        return friends.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> friendMap = new HashMap<>();
                    friendMap.put("id", entry.getKey());
                    friendMap.put("status", entry.getValue());
                    return friendMap;
                })
                .collect(Collectors.toList());
    }

    public void setFriends(Map<Integer, String> friends) {
        this.friends.clear();
        if (friends != null) {
            this.friends.putAll(friends);
        }
    }

    @JsonIgnore
    public Map<Integer, String> getFriends() {
        return new HashMap<>(friends);
    }
}