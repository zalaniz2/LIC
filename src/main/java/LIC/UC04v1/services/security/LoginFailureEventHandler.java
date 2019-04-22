package LIC.UC04v1.services.security;

import LIC.UC04v1.model.User;
import LIC.UC04v1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Created by bingyang.wei on 5/16/2017.
 */
@Component
public class LoginFailureEventHandler implements ApplicationListener<LoginFailureEvent>{

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(LoginFailureEvent loginFailureEvent) {
        Authentication authentication = (Authentication) loginFailureEvent.getSource();
        System.out.println("LoginEvent Failure for: " + authentication.getPrincipal());
        updateUserAccount(authentication);
    }

    public void updateUserAccount(Authentication authentication){
        User user = userService.findByUserName((String) authentication.getPrincipal());

        if(user != null){//user found
            user.setFailedLoginAttemptes(user.getFailedLoginAttemptes() + 1);
            if(user.getFailedLoginAttemptes() > 5){
                user.setEnabled(false);
                System.out.println("LOCK OUT!!!");
            }
            System.out.println("Valid User name, updating failed attempts");
            userService.saveOrUpdate(user);
        }
    }
}