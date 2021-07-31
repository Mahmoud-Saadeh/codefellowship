package com.example.codefellowship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
class CodefellowshipApplicationTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }
    @Test
    public void testHomeRoute() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print())
                .andExpect(view().name("index"));
    }
    @Test
    public void testUsersRoute() throws Exception {
        this.mockMvc.perform(get("/users")).andDo(print())
                .andExpect(view().name("users"));
    }
    @Test
    public void testAuthRoutes() throws Exception {
        this.mockMvc.perform(get("/login")).andDo(print())
                .andExpect(view().name("login"));
        this.mockMvc.perform(get("/signup")).andDo(print())
                .andExpect(view().name("signup"));
    }
}
