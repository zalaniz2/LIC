package LIC.UC04v1.services.security;

import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Created by bingyang.wei on 5/15/2017.
 */
@Aspect
@Component
public class LoginAspect {

    private LoginFailureEventPublisher loginFailureEventPublisher;
    private LoginSuccessEventPublisher successEventPublisher;

    @Autowired
    public void setLoginFailureEventPublisher(LoginFailureEventPublisher loginFailureEventPublisher) {
        this.loginFailureEventPublisher = loginFailureEventPublisher;
    }
    @Autowired
    public void setSuccessEventPublisher(LoginSuccessEventPublisher successEventPublisher) {
        this.successEventPublisher = successEventPublisher;
    }

    @Pointcut("execution(* org.springframework.security.authentication.AuthenticationProvider.authenticate(..))")
    public void doAuthenticate(){

    }
    @Before("LIC.UC04v1.services.security.LoginAspect.doAuthenticate() && args(authentication)")
    public void logBefore(Authentication authentication){
        System.out.println("This is before the Authentication Method: authentication:" + authentication.isAuthenticated());
    }

    @AfterReturning(value="LIC.UC04v1.services.security.LoginAspect.doAuthenticate()", returning="authentication")
    public void logAfterAuthenticate(Authentication authentication){
        System.out.println("This is after the Authenticate Method authentication: " + authentication.isAuthenticated());
        successEventPublisher.publish(new LoginSuccessEvent(authentication));

    }
    @AfterThrowing("LIC.UC04v1.services.security.LoginAspect.doAuthenticate() && args(authentication)")
    public void logAuthenticationException(Authentication authentication){
        String userDetails = (String) authentication.getPrincipal();
        System.out.println("Login failed for user: " + userDetails);
        loginFailureEventPublisher.publish(new LoginFailureEvent(authentication));
    }

}