package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
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
        int newId = generateId();
        User newUser = user.toBuilder().id(newId).build();
        users.put(newId, newUser);
        return newUser;
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("User with ID " + user.getId() + " not found.");
        }
        users.put(user.getId(), user);
        return user;
    }

    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("User with ID " + id + " not found.");
        }
        return users.get(id);
    }

    public void addFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public Set<Integer> getUserFriends(int userId) {
        return getUserById(userId).getFriends();
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> userFriends = getUserFriends(userId);
        Set<Integer> otherFriends = getUserFriends(otherId);
        userFriends.retainAll(otherFriends);
        return userFriends.stream().map(this::getUserById).toList();
    }

    private int generateId() {
        return id++;
    }
}
