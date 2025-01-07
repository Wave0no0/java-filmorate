package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor // Добавляет конструктор для final полей (если они есть)
public class UserController {
    private final List<User> users = new ArrayList<>();

    @PostMapping
    public User addUser(@RequestBody User user) {
        validateUser(user);
        user.setId(users.size() + 1);  // Присваиваем новый ID
        users.add(user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validateUser(user);
        User existingUser = users.stream()
                .filter(u -> u.getId() == user.getId())
                .findFirst()
                .orElseThrow(() -> new ValidationException("Пользователь с таким ID не найден."));
        users.set(user.getId() - 1, user);  // Исправляем ID, чтобы он корректно обновлял пользователя
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return users;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'.");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелов.");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin()); // Имя по умолчанию — логин
        }
    }
}
