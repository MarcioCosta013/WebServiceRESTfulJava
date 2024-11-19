package com.marciocosta.todosimple.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.marciocosta.todosimple.models.User;
import com.marciocosta.todosimple.models.User.CreateUser;
import com.marciocosta.todosimple.models.User.UpdateUser;
import com.marciocosta.todosimple.models.dto.UserCreateDTO;
import com.marciocosta.todosimple.services.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> findById (@PathVariable Long id) { //O ResponseEntity trata a resposta que vai dar ao Frontend.
        User obj = this.userService.findById(id);
        return ResponseEntity.ok().body(obj);
    }
    
    @PostMapping
    //@Validated(CreateUser.class) //serve para validar de tudo está seguindo as regras estabelecidas no model. (refatorado)
    public ResponseEntity<Void> create(@Valid @RequestBody UserCreateDTO obj) { //o "Valid" indica qual dado vai ser validado e o "Requestbody" passa dados no corpo da mensagem e só deve ser usado para o create e o update.
        
        this.userService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        /*
         * Um builder(ServletUriComponentsBuilder) que vai pegar o contexto da requisição atual que estamos rodando(fromCurrentRequest)
         * ("do User e do localhost que estamos no momento"), adiciona um path na frente dele(path("/{id}")),
         * faz um build expandindo colocando o getId dentro do id do path(buildAndExpand(obj.getId()))
         */
        return ResponseEntity.created(uri).build(); //created é um status 201 / e dentro dele mostra o caminho que teriamos que buscar para encontrar esse resultado. build para fazer o build dessa entidade que nós geramos.
    }

    @PutMapping("/{id}")
    @Validated(UpdateUser.class)
    public ResponseEntity<Void> update(@Valid @RequestBody User obj, @PathVariable Long id) {
        
        obj.setId(id); //para garantir que é o mesmo id que estamos recebendo pelo objeto.
        this.userService.update(obj);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
