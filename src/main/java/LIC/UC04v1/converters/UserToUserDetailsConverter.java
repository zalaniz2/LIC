package LIC.UC04v1.converters;

import LIC.UC04v1.model.User;
import LIC.UC04v1.model.UserDetailsImpl;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Converts user model into userDetail for Spring to access
 */
@Component("userToUserDetails")
public class UserToUserDetailsConverter implements Converter<User, UserDetails>{
    @Override
    public UserDetails convert(User user) {

        UserDetailsImpl userDetails = new UserDetailsImpl();

        if(user != null) {

            userDetails.setUserName(user.getUsername());

            userDetails.setPassword(user.getEncryptedPassword());

            userDetails.setEnabled(user.getEnabled());

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getRole()));
            });
            userDetails.setAuthorities(authorities);
        }
        return userDetails;
    }
}