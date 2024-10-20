package com.marciocosta.todosimple.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marciocosta.todosimple.models.Task;
import com.marciocosta.todosimple.models.User;
import com.marciocosta.todosimple.repositories.TaskRepository;

@Service
public class TaksService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);

        return task.orElseThrow(()-> new RuntimeException(
            "Tarefa não encontrada: ID: " + id + ", Tipo:" + Task.class.getName()
        ));
    }

    public List<Task> findAllByUserId (Long userId){
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        return tasks;
    }

    @Transactional
    public Task create(Task obj){
        User user = this.userService.findById(obj.getUser().getId());
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
            throw new RuntimeException("Não é possível deletar pois há entidades relacionadas!");
        }
    }
}
