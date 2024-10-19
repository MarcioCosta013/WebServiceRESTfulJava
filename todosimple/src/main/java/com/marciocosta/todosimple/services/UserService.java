package com.marciocosta.todosimple.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marciocosta.todosimple.models.User;
import com.marciocosta.todosimple.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service //Onde fica as regra de negocio.
public class UserService {
    
    @Autowired
    private UserRepository userRepository;


    public User findById(Long id){

        Optional<User> user = this.userRepository.findById(id); //o Optional serve para se não tiver o usuario no bd ele retornar "vazio" em vez de null(evitando o NullExceptionPoint).
        return user.orElseThrow(() -> new RuntimeException( //para retornar só de vir algo, se vir empty(vazio) dispare essa excesão runtime que faz com que o programa não pare.
            "Usuario não encontrado! Id: " + id + ", Tipo: " + User.class.getName()
        ));
    }

    @Transactional //essa notação serve para "ou sava tudo ou nada", serve mais para creates e updates em um bd.
    public User create (User obj){
        obj.setId(null); // para impedir que o usuario use a o Create para atualizar um dado em um id já existente.
        obj = this.userRepository.save(obj); //serve para já salvar também se for criado com Task's. E é possivel fazer isso pelo usuário pelo na classe User tem a Task também.
        return obj;
    }

    @Transactional
    public User update (User obj){
        User newObj = findById(obj.getId()); //serve para verifica se realmente existe esse usuario. e já reutiliza o codigo acima do findById.
        newObj.setPassword(obj.getPassword());
        return this.userRepository.save(newObj);
    }

    public void delete (Long id){
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possivel excluir pois há entidades relacionadas!");
        }
    }
}
