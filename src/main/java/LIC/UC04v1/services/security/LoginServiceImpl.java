package LIC.UC04v1.services.security;

import LIC.UC04v1.model.User;
import LIC.UC04v1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by bingyang.wei on 5/16/2017.
 */
@Service
public class LoginServiceImpl implements LoginService {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(fixedRate = 60000)
    @Override
    public void resetFailedLogins() {
        System.out.println("Checking for locked accounts......");
        List<User> users = (List<User>) userService.listAll();

        users.forEach(user->{
            if(!user.getEnabled() && user.getFailedLoginAttemptes() > 0){
                System.out.println("Resetting failed attempts for user: " + user.getEmail());
                user.setFailedLoginAttemptes(0);
                user.setEnabled(true);
                userService.saveOrUpdate(user);
            }
        });
    }
}