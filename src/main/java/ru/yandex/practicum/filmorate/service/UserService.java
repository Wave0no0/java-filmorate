package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Optional<User> getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId); // Метод в хранилище для добавления друга
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.removeFriend(userId, friendId); // Метод в хранилище для удаления друга
    }

    public List<User> getFriends(int userId) {
        return userStorage.getFriends(userId); // Метод в хранилище для получения друзей
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId); // Метод в хранилище для получения общих друзей
    }
}
