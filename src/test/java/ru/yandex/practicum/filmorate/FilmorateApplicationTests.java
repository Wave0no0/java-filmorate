package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testAddUserWithValidData() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
						.contentType("application/json")
						.content("{\"email\": \"user@example.com\", \"login\": \"userlogin\", " +
								"\"name\": \"User Name\", \"birthday\": \"1990-01-01\"}"))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user@example.com"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.login").value("userlogin"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("User Name"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1990-01-01"));
	}

	@Test
	void testAddUserWithInvalidEmail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
						.contentType("application/json")
						.content("{\"email\": \"invalid_email\", \"login\": \"userlogin\", " +
								"\"name\": \"User Name\", \"birthday\": \"1990-01-01\"}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Электронная почта не может быть пустой и должна содержать символ '@'."));
	}

	@Test
	void testAddUserWithEmptyLogin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
						.contentType("application/json")
						.content("{\"email\": \"user@example.com\", \"login\": \"\", " +
								"\"name\": \"User Name\", \"birthday\": \"1990-01-01\"}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Логин не может быть пустым и не должен содержать пробелов."));
	}

	@Test
	void testAddUserWithFutureBirthday() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
						.contentType("application/json")
						.content("{\"email\": \"user@example.com\", \"login\": \"userlogin\", " +
								"\"name\": \"User Name\", \"birthday\": \"2100-01-01\"}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Дата рождения не может быть в будущем."));
	}

	@Test
	void testUpdateUserWithValidId() throws Exception {
		// Сначала добавляем пользователя
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
						.contentType("application/json")
						.content("{\"email\": \"user@example.com\", \"login\": \"userlogin\", " +
								"\"name\": \"User Name\", \"birthday\": \"1990-01-01\"}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		// Затем обновляем его данные
		mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
						.contentType("application/json")
						.content("{\"email\": \"updated@example.com\", \"login\": \"updatedlogin\", " +
								"\"name\": \"Updated Name\", \"birthday\": \"1995-05-15\"}"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updated@example.com"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.login").value("updatedlogin"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Name"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1995-05-15"));
	}

	@Test
	void testUpdateUserWithInvalidId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/users/999")
						.contentType("application/json")
						.content("{\"email\": \"updated@example.com\", \"login\": \"updatedlogin\", " +
								"\"name\": \"Updated Name\", \"birthday\": \"1995-05-15\"}"))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Пользователь с таким ID не найден."));
	}

	@Test
	void testGetAllUsers() throws Exception {
		// Добавляем нескольких пользователей
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
						.contentType("application/json")
						.content("{\"email\": \"user1@example.com\", \"login\": \"userlogin1\", " +
								"\"name\": \"User Name 1\", \"birthday\": \"1985-01-01\"}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		mockMvc.perform(MockMvcRequestBuilders.post("/users")
						.contentType("application/json")
						.content("{\"email\": \"user2@example.com\", \"login\": \"userlogin2\", " +
								"\"name\": \"User Name 2\", \"birthday\": \"1990-01-01\"}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		// Проверяем, что список пользователей возвращается корректно
		mockMvc.perform(MockMvcRequestBuilders.get("/users"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
	}
}
