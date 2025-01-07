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
class FilmorateApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void testAddFilmWithValidData() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/films")
						.contentType("application/json")
						.content("{\"name\": \"Inception\", \"description\": \"A mind-bending thriller.\", " +
								"\"releaseDate\": \"2010-07-16\", \"duration\": 148}"))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Inception"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value("A mind-bending thriller."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("2010-07-16"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(148));
	}

	@Test
	void testAddFilmWithInvalidName() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/films")
						.contentType("application/json")
						.content("{\"name\": \"\", \"description\": \"Some description.\", " +
								"\"releaseDate\": \"2025-01-01\", \"duration\": 120}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Название фильма не может быть пустым."));
	}

	@Test
	void testAddFilmWithInvalidReleaseDate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/films")
						.contentType("application/json")
						.content("{\"name\": \"Film\", \"description\": \"Some description.\", " +
								"\"releaseDate\": \"1800-01-01\", \"duration\": 90}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error")
						.value("Дата релиза не может быть раньше 28 декабря 1895 года."));
	}

	@Test
	void testGetAllFilms() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/films"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
	}

	@Test
	void testUpdateFilmWithInvalidId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/films")
						.contentType("application/json")
						.content("{\"id\": 999, \"name\": \"Updated Film\", " +
								"\"description\": \"Updated description.\", " +
								"\"releaseDate\": \"2025-01-01\", \"duration\": 120}"))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Фильм с таким ID не найден."));
	}

	@Test
	void testFilmIdGeneration() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/films")
						.contentType("application/json")
						.content("{\"name\": \"The Dark Knight\", \"description\": \"A Batman film.\", " +
								"\"releaseDate\": \"2008-07-18\", \"duration\": 152}"))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2));
	}

	@Test
	void testUpdateFilmWithValidId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/films/1")
						.contentType("application/json")
						.content("{\"id\": 1, \"name\": \"Updated Film\", " +
								"\"description\": \"Updated description.\", " +
								"\"releaseDate\": \"2025-01-01\", \"duration\": 120}"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Film"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated description."));
	}
}