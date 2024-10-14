package com.marciocosta.todosimple.models;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table (name = User.TABLE_NAME)
public class User {
    public static final String TABLE_NAME = "user"; //para ter certeza que esse vai ser o nome da table.

    public interface CreateUser{};
    public interface UpdateUser {}; //interface só para não deixar modificar o identificador(username) depois de criado.

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", unique= true)
    private Long id;

    @Column(name="username", length=100,nullable=false,unique=true)
    @NotNull(groups=CreateUser.class)
    @NotEmpty(groups=CreateUser.class)  
    @Size(groups=CreateUser.class, min=2,max = 100)
    private String username;

    @Column(name = "password", nullable=false, length=60)
    @NotNull (groups= {CreateUser.class, UpdateUser.class})
    @NotEmpty(groups= {CreateUser.class, UpdateUser.class})
    @Size(groups={CreateUser.class, UpdateUser.class}, min = 6, max=60)
    private String password;

    //private List<Task> tasks = new ArrayList<Task>;


    public User() {
    }


    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
