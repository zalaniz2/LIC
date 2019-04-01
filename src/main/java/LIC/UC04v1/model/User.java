package LIC.UC04v1.model;

import LIC.UC04v1.model.Role;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingyang.wei on 5/9/2017.
 */
@Entity
public class User extends AbstractDomainObject{

    private String username;



    private String email;

    @Transient
    private String password;

    private String encryptedPassword;
    private Boolean enabled = true;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
    private List<Role> roles = new ArrayList<>();
    private Integer failedLoginAttemptes = 0;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    public void addRole(Role role){
        if(!roles.contains(role)){
            this.roles.add(role);
        }
        if(!role.getUsers().contains(this)){
            role.getUsers().add(this);
        }
    }
    public void removeRole(Role role){
        this.roles.remove(role);
        role.getUsers().remove(this);
    }

    public Integer getFailedLoginAttemptes() {
        return failedLoginAttemptes;
    }

    public void setFailedLoginAttemptes(Integer failedLoginAttemptes) {
        this.failedLoginAttemptes = failedLoginAttemptes;
    }
}

