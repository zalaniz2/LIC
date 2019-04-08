package LIC.UC04v1.services.security;

import LIC.UC04v1.model.User;
import LIC.UC04v1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by bingyang.wei on 5/15/2017.
 */
@Service
public class SecUserDetailsService implements UserDetailsService{

    private UserService userService;
    private Converter<User, UserDetails> userUserDetailsConverter;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    //Qualifier specifies which bean to inject if there are several classes that implement the same interface
    @Qualifier(value="userToUserDetails")
    public void setUserUserDetailsConverter(Converter<User, UserDetails> userUserDetailsConverter) {
        this.userUserDetailsConverter = userUserDetailsConverter;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //
        return userUserDetailsConverter.convert(userService.findByUserName(s)); //findUserByEmail method defined in user service
    }

}

