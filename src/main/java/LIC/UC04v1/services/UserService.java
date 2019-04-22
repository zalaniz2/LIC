package LIC.UC04v1.services;


import LIC.UC04v1.model.User;
import org.springframework.stereotype.Service;



public interface UserService extends CRUDService<User>{

    public User findByUserName(String name);


}