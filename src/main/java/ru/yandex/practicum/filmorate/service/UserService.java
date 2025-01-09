package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        log.info("Adding new user: {}", user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        log.info("Updating user: {}", user);
        Optional<User> existingUser = userStorage.getUserById(user.getId());
        if (existingUser.isEmpty()) {
            log.error("User with ID {} not found", user.getId());
            throw new NotFoundException("User with ID " + user.getId() + " not found");
        }
        return userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        log.info("Retrieving user with ID: {}", id);
        return userStorage.getUserById(id).orElseThrow(() -> {
            log.error("User with ID {} not found", id);
            return new NotFoundException("User with ID " + id + " not found");
        });
    }

    public List<User> getAllUsers() {
        log.info("Retrieving all users");
        return userStorage.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        log.info("Adding friend: {} to user: {}", friendId, userId);
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void removeFriend(int userId, int friendId) {
        log.info("Removing friend: {} from user: {}", friendId, userId);
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public List<User> getUserFriends(int userId) {
        log.info("Retrieving friends for user: {}", userId);
        User user = getUserById(userId);
        return user.getFriends().stream()
                .map(this::getUserById)
                .toList();
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        log.info("Retrieving common friends for users: {} and {}", userId, otherUserId);
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);
        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(this::getUserById)
                .toList();
    }
}
