package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addFriend(int userId, int friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return user;
    }

    public User removeFriend(int userId, int friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        return user;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> userFriends = new HashSet<>(getUserOrThrow(userId).getFriends());
        Set<Integer> otherFriends = new HashSet<>(getUserOrThrow(otherId).getFriends());

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(id -> userStorage.getUserById(id).orElseThrow())
                .collect(Collectors.toList());
    }

    public List<User> getFriends(int userId) {
        return getUserOrThrow(userId).getFriends().stream()
                .map(id -> userStorage.getUserById(id).orElseThrow())
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(int id) {
        return userStorage.getUserById(id).orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }
}
