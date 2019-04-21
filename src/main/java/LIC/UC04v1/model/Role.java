package LIC.UC04v1.model;

import LIC.UC04v1.model.AbstractDomainObject;
import LIC.UC04v1.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingyang.wei on 5/9/2017.
 */
@Entity
public class Role extends AbstractDomainObject{
    private String role;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(User user){
        if(!this.users.contains(user)){
            this.users.add(user);
        }
        if(!user.getRoles().contains(this)){
            user.getRoles().add(this);
        }
    }
    public void removeUser(User user){
        this.users.remove(user);
        user.getRoles().remove(this);
    }
}