package com.marciocosta.todosimple.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.marciocosta.todosimple.models.enums.ProfileEnum;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table (name = User.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    public static final String TABLE_NAME = "user"; //para ter certeza que esse vai ser o nome da table.

    public interface CreateUser{
    }
    public interface UpdateUser {
    }
    //interface só para não deixar modificar o identificador(username) depois de criado.

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", unique= true)
    private Long id;

    @Column(name="username", length=100,nullable=false,unique=true)
    @NotNull(groups=CreateUser.class)
    @NotEmpty(groups=CreateUser.class)  
    @Size(groups=CreateUser.class, min=2,max = 100)
    private String username;

    @JsonProperty(access = Access.WRITE_ONLY) //Para só escrever a senha e não retornar para o front.
    @Column(name = "password", nullable=false, length=60)
    @NotNull (groups= {CreateUser.class, UpdateUser.class})
    @NotEmpty(groups= {CreateUser.class, UpdateUser.class})
    @Size(groups={CreateUser.class, UpdateUser.class}, min = 6, max=60)
    private String password;

    @OneToMany(mappedBy = "user") //um user pode ter varias task's //em mappedBy que é obrigatorio, tem que colocar o nome da variavel a qual ele corresponde.
    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Task> tasks = new ArrayList<Task>();

    @Column(name = "profile", nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_profile")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Integer> profiles = new HashSet<>();

    public Set<ProfileEnum> getProfiles(){
        return this.profiles.stream().map(X -> ProfileEnum.toEnum(X)).collect(Collectors.toSet());
    }
}
