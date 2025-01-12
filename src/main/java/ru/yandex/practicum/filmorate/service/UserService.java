package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
        user.validate();
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        user.validate();
        return userStorage.updateUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + user.getId() + " not found."));
    }

    public void addFriend(int userId, int friendId) {
        User user = getValidUser(userId);
        User friend = getValidUser(friendId);
        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
    }

    public void removeFriend(int userId, int friendId) {
        User user = getValidUser(userId);
        User friend = getValidUser(friendId);
        user.removeFriend(friend.getId());
        friend.removeFriend(user.getId());
    }

    public Set<User> getFriends(int userId) {
        return getValidUser(userId).getFriends().stream()
                .map(this::getValidUser)
                .collect(Collectors.toSet());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> userFriends = getValidUser(userId).getFriends();
        Set<Integer> otherFriends = getValidUser(otherId).getFriends();
        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getValidUser)
                .toList();
    }

    public User getValidUser(int userId) {
        return getUserById(userId);
    }
}
