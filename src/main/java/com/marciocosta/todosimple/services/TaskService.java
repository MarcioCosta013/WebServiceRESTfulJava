package com.marciocosta.todosimple.services;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marciocosta.todosimple.models.Task;
import com.marciocosta.todosimple.models.User;
import com.marciocosta.todosimple.models.enums.ProfileEnum;
import com.marciocosta.todosimple.models.projection.TaskProjection;
import com.marciocosta.todosimple.repositories.TaskRepository;
import com.marciocosta.todosimple.security.UserSpringSecurity;
import com.marciocosta.todosimple.services.exceptions.AuthorizationException;
import com.marciocosta.todosimple.services.exceptions.DataBindingViolationException;
import com.marciocosta.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Task task = this.taskRepository.findById(id).orElseThrow(()-> new ObjectNotFoundException(
            "Tarefa não encontrada: ID: " + id + ", Tipo:" + Task.class.getName()));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIM) && !userHasTask(userSpringSecurity, task)) {
                throw new AuthorizationException("Acesso Negado");
        }
        return task;
    }

    public List<TaskProjection> findAllByUser (){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity)) {
                throw new AuthorizationException("Acesso Negado");
        }
        
        List<TaskProjection> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    @Transactional
    public Task create(Task obj){

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity)) {
                throw new AuthorizationException("Acesso Negado");
        }
        
        User user = this.userService.findById(userSpringSecurity.getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(Long id){
        findById(id);

        //Não precisaria fazer esse tratamento pq a task no BD não vai está como chave estrageira em outra tabela como por exemplo o User.
        //Mas já vou deixar tratado caso no futuro ela seja relacionada a outra entidade.
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível deletar pois há entidades relacionadas!");
        }
    }

    private boolean userHasTask (UserSpringSecurity userSpringSecurity, Task task){
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }
}
