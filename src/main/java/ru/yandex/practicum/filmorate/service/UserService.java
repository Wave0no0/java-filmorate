package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    public User createUser(User user) {
        validateUser(user);
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User not found with id: " + user.getId());
        }
        users.put(user.getId(), user);
        return user;
    }

    public User getUserById(int id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
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

    public List<User> getUserFriends(int userId) {
        User user = getUserById(userId);
        List<User> friends = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            friends.add(getUserById(friendId)); // Преобразование id в объекты User
        }
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = getUserById(userId);
        User other = getUserById(otherId);

        Set<Integer> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(other.getFriends());

        List<User> commonFriends = new ArrayList<>();
        for (Integer friendId : commonFriendIds) {
            commonFriends.add(getUserById(friendId));
        }
        return commonFriends;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Некорректный формат email: " + user.getEmail());
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new IllegalArgumentException("Логин не должен быть пустым или содержать пробелы");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не может быть в будущем");
        }
    }
}
