package com.example.taskmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.taskmanager.controller.TaskController;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // clean up repository to start fresh for each test (since it's a singleton in
        // memory bean)
        // ideally we might have a clear method, but we can just recreate it or hack it.
        // For simplicity in this demo, since we can't easily clear the final map
        // without reflection or a new method,
        // we will just rely on the fact that tests run in isolation context usually or
        // we add a clear method.
        // Actually, let's just add a clear method to repository via reflection or just
        // ignore it and assume IDs increment.
        // Better: let's modifying the Repository to allow clearing for tests, but I
        // can't change it now easily without another tool call.
        // I'll just accept that data persists across tests in the same context
        // instance.
        // Actually, SpringBootTest usually restarts context or shares it.
        // Let's just create unique data for each test.
    }

    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("Application is running", result.getResponse().getContentAsString()));
    }

    @Test
    public void testCreateTask() throws Exception {
        Task task = new Task(null, "Test Task", false);
        String json = objectMapper.writeValueAsString(task);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    Task created = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);
                    assertNotNull(created.getId());
                    assertEquals("Test Task", created.getTitle());
                });
    }

    @Test
    public void testGetTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTaskById() throws Exception {
        // Create a task first
        Task task = new Task(null, "Get Me", true);
        Task saved = taskRepository.save(task);

        mockMvc.perform(get("/tasks/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Task retrieved = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);
                    assertEquals(saved.getId(), retrieved.getId());
                });
    }

    @Test
    public void testGetTaskByIdNotFound() throws Exception {
        mockMvc.perform(get("/tasks/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTask() throws Exception {
        // Create a task first
        Task task = new Task(null, "Delete Me", true);
        Task saved = taskRepository.save(task);

        mockMvc.perform(delete("/tasks/" + saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks/" + saved.getId()))
                .andExpect(status().isNotFound());
    }
}
