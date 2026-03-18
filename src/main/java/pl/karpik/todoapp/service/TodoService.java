package pl.karpik.todoapp.service;

import org.springframework.stereotype.Service;
import pl.karpik.todoapp.model.Todo;
import pl.karpik.todoapp.repository.TodoRepository;


import java.util.List;


@Service
public class TodoService {
    private final TodoRepository todoRepository;
    public TodoService (TodoRepository todoRepository)
    {
        this.todoRepository = todoRepository;
    }

    public void save (Todo todo)
    {
        todoRepository.save(todo);
    }
    public List<Todo> findAll()
    {
        return todoRepository.findAll()
                .stream()
                .sorted((a,b) -> Boolean.compare(a.isCompleted(), b.isCompleted()))
                .toList();
    }

    public void delete(Todo todo)
    {
        todoRepository.delete(todo);
    }

    public void toggle(Todo todo) {
        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);
    }

    public List<Todo> findActive() {
        return todoRepository.findAll()
                .stream()
                .sorted((a,b) -> {
                    int completedCompare = Boolean.compare(a.isCompleted(), b.isCompleted());

                    if (completedCompare != 0){
                        return completedCompare;
                    }

                    if (a.getDeadline() == null && b.getDeadline() == null) {
                        return 0;
                    }

                    if (a.getDeadline() == null){
                        return 1;
                    }
                    if (b.getDeadline() == null){
                        return -1;
                    }
                    return a.getDeadline().compareTo(b.getDeadline());
                })
                .toList();
    }

    public void update(Todo todo, String newTitle) {
        todo.setTitle(newTitle);
        todoRepository.save(todo);

    }

}

