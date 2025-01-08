package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final List<User> users = new ArrayList<>();

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            validateUser(user);
            user.setId(users.size() + 1); // Присваиваем новый ID
            users.add(user);
            log.info("Пользователь успешно добавлен: {}", user);
            return ResponseEntity.status(201).body(user);
        } catch (ValidationException e) {
            log.error("Ошибка при добавлении пользователя: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user) {
        try {
            validateUser(user);
            User existingUser = users.stream()
                    .filter(u -> u.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ValidationException("Пользователь с таким ID не найден."));
            existingUser.setEmail(user.getEmail());
            existingUser.setLogin(user.getLogin());
            existingUser.setName(user.getName());
            existingUser.setBirthday(user.getBirthday());
            log.info("Пользователь успешно обновлен: {}", existingUser);
            return ResponseEntity.ok(existingUser);
        } catch (ValidationException e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей.");
        return ResponseEntity.ok(users);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации: Электронная почта не может быть пустой и должна содержать символ '@'.");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'.");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации: Логин не может быть пустым и не должен содержать пробелов.");
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелов.");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: Дата рождения не может быть в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Имя пользователя не задано, будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin()); // Имя по умолчанию — логин
        }
    }
}
