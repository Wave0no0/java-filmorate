package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        validateUser(user);
        // Проверка уникальности email и логина
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new ValidationException("Email already exists.");
        }
        if (users.values().stream().anyMatch(u -> u.getLogin().equals(user.getLogin()))) {
            throw new ValidationException("Login already exists.");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User with ID " + user.getId() + " not found.");
        }
        users.put(user.getId(), user);
        return user;
    }

    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with ID " + id + " not found.");
        }
        return users.get(id);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email cannot be null or blank.");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Invalid email format.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Login cannot be null or blank.");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login must not contain spaces.");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(java.time.LocalDate.now())) {
            throw new ValidationException("Birthday must be in the past or present.");
        }
    }

    private int generateId() {
        return id++;
    }
}
