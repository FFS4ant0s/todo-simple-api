package com.fernandosantos.todosimple.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fernandosantos.todosimple.Security.UserSpringSecurity;
import com.fernandosantos.todosimple.models.Task;
import com.fernandosantos.todosimple.models.User;
import com.fernandosantos.todosimple.models.enums.ProfileEnum;
import com.fernandosantos.todosimple.repositories.TaskRepository;
import com.fernandosantos.todosimple.services.exceptions.AuthorizationException;

import jakarta.transaction.Transactional;
import java.util.Objects;

@Service
public class TaskService {
    @Autowired
    private UserService userService;

    @Autowired
    private TaskRepository taskRepository;

    public Task findById(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new RuntimeException(
                "Tarefa não encontrada! Id: " + id + ", Tipo: " + User.class.getName()));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity)
                || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task))
            throw new AuthorizationException("Acesso negado!");

        return task;
    }

    public List<Task> findAllByUser() {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");

        List<Task> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    @Transactional
    public Task create(Task obj) {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");

        User user = this.userService.findById(userSpringSecurity.getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task update(Task obj) {
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possível excluir pois há entidades relacionadas.");
        }
    }

    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task) {
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }
}
