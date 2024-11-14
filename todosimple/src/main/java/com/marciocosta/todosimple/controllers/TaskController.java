package com.marciocosta.todosimple.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.marciocosta.todosimple.models.Task;
import com.marciocosta.todosimple.services.TaksService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/task")
@Validated
public class TaskController {

    @Autowired
    private TaksService taksService;


    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        Task obj = this.taksService.findById(id);
        return ResponseEntity.ok(obj);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Task>> findAllByUser() {

        List<Task> objs = this.taksService.findAllByUser();
        return ResponseEntity.ok().body(objs);
    }
    
    
    @PostMapping
    @Validated
    public ResponseEntity<Void> create(@Valid @RequestBody Task obj) {
        
        this.taksService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @Validated
    public ResponseEntity<Void> upadate( @Valid @RequestBody Task obj, @PathVariable Long id){

        obj.setId(id);
        this.taksService.update(obj);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        this.taksService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
