package LIC.UC04v1.services.security;

import org.springframework.context.ApplicationEvent;

/**
 * Created by bingyang.wei on 5/16/2017.
 */
public class LoginFailureEvent extends ApplicationEvent{
    public LoginFailureEvent(Object source) {
        super(source);
    }
}
