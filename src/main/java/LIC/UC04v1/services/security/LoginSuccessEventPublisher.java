package LIC.UC04v1.services.security;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * Created by bingyang.wei on 5/16/2017.
 */
@Component
public class LoginSuccessEventPublisher implements ApplicationEventPublisherAware{

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    public void publish(LoginSuccessEvent event){
        this.publisher.publishEvent(event);
    }
}