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
        checkUniqueEmailAndLogin(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User with ID " + user.getId() + " not found.");
        }
        checkUniqueEmailAndLogin(user);
        users.put(user.getId(), user);
        return user;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Invalid email format.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot be null, blank, or contain spaces.");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(java.time.LocalDate.now())) {
            throw new ValidationException("Birthday must be in the past or present.");
        }
    }

    private void checkUniqueEmailAndLogin(User user) {
        for (User existingUser : users.values()) {
            if (existingUser.getId() != user.getId() && existingUser.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Email already exists.");
            }
            if (existingUser.getId() != user.getId() && existingUser.getLogin().equals(user.getLogin())) {
                throw new ValidationException("Login already exists.");
            }
        }
    }

    private int generateId() {
        return id++;
    }
}
