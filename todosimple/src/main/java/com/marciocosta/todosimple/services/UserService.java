package com.marciocosta.todosimple.services;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.marciocosta.todosimple.models.User;
import com.marciocosta.todosimple.models.dto.UserCreateDTO;
import com.marciocosta.todosimple.models.dto.UserUpdateDTO;
import com.marciocosta.todosimple.models.enums.ProfileEnum;
import com.marciocosta.todosimple.repositories.UserRepository;
import com.marciocosta.todosimple.security.UserSpringSecurity;
import com.marciocosta.todosimple.services.exceptions.AuthorizationException;
import com.marciocosta.todosimple.services.exceptions.DataBindingViolationException;
import com.marciocosta.todosimple.services.exceptions.ObjectNotFoundException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service //Onde fica as regra de negocio.
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public User findById(Long id){
        UserSpringSecurity userSpringSecurity = authenticated();
        if(!Objects.nonNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIM) && !id.equals(userSpringSecurity.getId())){
            throw new AuthorizationException("Acesso negado!");
        }

        Optional<User> user = this.userRepository.findById(id); //o Optional serve para se não tiver o usuario no bd ele retornar "vazio" em vez de null(evitando o NullExceptionPoint).
        return user.orElseThrow(() -> new ObjectNotFoundException( //para retornar só de vir algo, se vir empty(vazio) dispare essa excesão runtime que faz com que o programa não pare.
            "Usuario não encontrado! Id: " + id + ", Tipo: " + User.class.getName()
        ));
    }

    @Transactional //essa notação serve para "ou sava tudo ou nada", serve mais para creates e updates em um bd.
    public User create (User obj){
        obj.setId(null); // para impedir que o usuario use a o Create para atualizar um dado em um id já existente.
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword())); //Para criptografar a senha antes de salvar no BD.
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet())); // ???
        obj = this.userRepository.save(obj); //serve para já salvar também se for criado com Task's. E é possivel fazer isso pelo usuário pelo na classe User tem a Task também.
        return obj;
    }

    @Transactional
    public User update (User obj){
        User newObj = findById(obj.getId()); //serve para verifica se realmente existe esse usuario. e já reutiliza o codigo acima do findById.
        newObj.setPassword(obj.getPassword());
        newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        return this.userRepository.save(newObj);
    }

    public void delete (Long id){
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possivel excluir pois há entidades relacionadas!");
        }
    }

    public static UserSpringSecurity authenticated (){
    /*
     * Criada para controlar quem pode ter acesso as informações, ou adm ou usuário comum.
     */
        try {
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

    public User fromDTO(@Valid UserCreateDTO obj){

        User user = new User();

        user.setUsername(obj.getUsername());
        user.setPassword(obj.getPassword());

        return user;
    }

    public User fromDTO(@Valid UserUpdateDTO obj){
        User user = new User();
        user.setId(obj.getId());
        user.setPassword(obj.getPassword());
        return user;
    }
}
