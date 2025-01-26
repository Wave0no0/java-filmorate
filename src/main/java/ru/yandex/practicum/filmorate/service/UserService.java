package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        log.info("Получение пользователя с ID {}", id);
        return userStorage.getUser(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", id);
                    return new ResourceNotFoundException("Пользователь с ID " + id + " не найден.");
                });
    }

    public User createUser(User user) {
        log.info("Создание нового пользователя: {}", user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        log.info("Обновление пользователя с ID {}", user.getId());
        if (!userStorage.getUser(user.getId()).isPresent()) {
            log.warn("Пользователь с ID {} не найден для обновления", user.getId());
            throw new ResourceNotFoundException("Пользователь с ID " + user.getId() + " не найден.");
        }
        return userStorage.updateUser(user)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID " + user.getId() + " не найден.");
                });
    }

    public void addFriend(int userId, int friendId) {
        log.info("Добавление в друзья пользователя с ID {} к пользователю с ID {}", friendId, userId);

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (Objects.equals(userId, friendId)) {
            log.warn("Попытка добавить пользователя с ID {} в друзья самого себя", userId);
            throw new IllegalArgumentException("Нельзя добавить себя в друзья.");
        }

        if (user.getFriends().contains(friendId)) {
            log.warn("Пользователь с ID {} уже в друзьях у пользователя с ID {}", friendId, userId);
            return;
        }

        userStorage.addFriend(userId, friendId);
        log.info("Пользователь с ID {} успешно добавлен в друзья пользователя с ID {}", friendId, userId);
    }

    public void removeFriend(int userId, int friendId) {
        log.info("Удаление из друзей пользователя с ID {} у пользователя с ID {}", friendId, userId);

        User user = getUserById(userId);

        userStorage.getUser(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + friendId + " не найден."));

        if (!user.getFriends().contains(friendId)) {
            log.warn("Пользователь с ID {} не является другом пользователя с ID {}", friendId, userId);
            return;
        }

        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь с ID {} удалён из друзей пользователя с ID {}", friendId, userId);
    }

    public Set<User> getFriends(int userId) {
        log.info("Получение списка друзей пользователя с ID {}", userId);

        getUserById(userId); // Проверяем наличие пользователя

        return userStorage.getFriends(userId).stream()
                .map(this::getUserById)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(int userId, int otherId) {
        log.info("Получение общих друзей между пользователями с ID {} и ID {}", userId, otherId);

        getUserById(userId); // Проверяем наличие первого пользователя
        getUserById(otherId); // Проверяем наличие второго пользователя

        Set<Integer> userFriends = userStorage.getFriends(userId);
        Set<Integer> otherFriends = userStorage.getFriends(otherId);

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getUserById)
                .collect(Collectors.toSet());
    }
}
