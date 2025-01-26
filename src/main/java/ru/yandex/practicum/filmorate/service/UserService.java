package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));
    }

    public User createUser(User user) {
        preSave(user);
        user.validate();
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        user.validate();
        return userStorage.updateUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + user.getId() + " not found."));
    }

    public User addFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        userStorage.addFriend(userId, friendId);
        return getUserById(userId);
    }

    public void confirmFriendship(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.updateFriendStatus(friendId, "подтверждённая");
        friend.updateFriendStatus(userId, "подтверждённая");

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void removeFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> userFriends = new HashSet<>(userStorage.getFriends(userId)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet()));

        Set<Integer> otherFriends = new HashSet<>(userStorage.getFriends(otherId)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet()));

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }


    public List<User> getFriends(int userId) {
        // Проверяем, существует ли пользователь
        getUserById(userId); // Если пользователь не существует, выбрасывается ResourceNotFoundException

        // Возвращаем друзей
        return userStorage.getFriends(userId);
    }

    private void preSave(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}