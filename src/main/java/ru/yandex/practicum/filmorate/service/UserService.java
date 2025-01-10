package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User with ID " + user.getId() + " not found.");
        }
        users.put(user.getId(), user);
        return user;
    }

    public User getUserById(int id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found."));
    }

    private int generateId() {
        return id++;
    }
}
