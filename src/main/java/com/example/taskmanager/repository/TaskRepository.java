package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * In-memory repository for managing tasks.
 */
@Repository
public class TaskRepository {

    private final ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Saves a task. If it's a new task (id is null), assigns an ID.
     *
     * @param task the task to save
     * @return the saved task
     */
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public boolean deleteById(Long id) {
        return tasks.remove(id) != null;
    }
}
