package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Transactional
public class UserDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserDbStorage userDbStorage;

    @BeforeEach
    public void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
        jdbcTemplate.execute("DELETE FROM friends");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        System.out.println("Database cleaned and auto-increment reset.");
    }

    @Test
    public void testAddUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User addedUser = userDbStorage.addUser(user);

        assertNotNull(addedUser.getId());
        assertEquals("test@example.com", addedUser.getEmail());
        assertEquals("testuser", addedUser.getLogin());
    }

    @Test
    public void testGetUser() {
        User user = new User();
        user.setEmail("test2@example.com");
        user.setLogin("user2");
        user.setName("User Two");
        user.setBirthday(LocalDate.of(1995, 5, 15));

        User addedUser = userDbStorage.addUser(user);

        Optional<User> fetchedUser = userDbStorage.getUser(addedUser.getId());
        assertTrue(fetchedUser.isPresent());
        assertEquals("test2@example.com", fetchedUser.get().getEmail());
        assertEquals("user2", fetchedUser.get().getLogin());
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("update@example.com");
        user.setLogin("updateuser");
        user.setName("Update User");
        user.setBirthday(LocalDate.of(1985, 12, 25));

        User addedUser = userDbStorage.addUser(user);

        addedUser.setName("Updated Name");
        addedUser.setEmail("updated@example.com");
        Optional<User> updatedUser = userDbStorage.updateUser(addedUser);

        assertTrue(updatedUser.isPresent());
        assertEquals("Updated Name", updatedUser.get().getName());
        assertEquals("updated@example.com", updatedUser.get().getEmail());
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1995, 5, 15));

        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);

        assertEquals(2, userDbStorage.getAllUsers().size());
    }
}