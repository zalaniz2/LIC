package LIC.UC04v1.services;


import LIC.UC04v1.model.Role;
import LIC.UC04v1.model.User;
import LIC.UC04v1.repositories.RoleRepository;
import LIC.UC04v1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service("userService")
public interface UserService extends CRUDService<User>{

    public User findUserByEmail(String email);


}