package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> getUser(int id);

    User addUser(User user);

    Optional<User> updateUser(User user);

    List<User> getAllUsers();
}
