package com.github.thundermarket.thundermarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thundermarket.thundermarket.domain.User;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ProductDetailControllerTest {

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("schema.sql");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Container
    public static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:6.2-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User createUser(String email, String password) {
        return new User.Builder()
                .withEmail(email)
                .withPassword(password)
                .build();
    }

    @Test
    public void 상품상세정보_존재하지않으면_404응답() throws Exception {
        User user = createUser("test01@email.com", "password");
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/api/v1/auth/join")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"))
                .andReturn();

        String sessionId = loginResult.getResponse().getCookie("SESSION").getValue();

        mockMvc.perform(get("/api/v1/products/0")
                        .contentType("application/json")
                        .cookie(new Cookie("SESSION", sessionId)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql("/productDetailControllerTest.sql")
    public void 상품상세정보_존재하면_200응답() throws Exception {
        User user = createUser("test01@email.com", "password");

        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/api/v1/auth/join")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"))
                .andReturn();

        String sessionId = loginResult.getResponse().getCookie("SESSION").getValue();

        mockMvc.perform(get("/api/v1/products/1")
                        .contentType("application/json")
                        .cookie(new Cookie("SESSION", sessionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productDetail.color").value("white"));
    }
}
