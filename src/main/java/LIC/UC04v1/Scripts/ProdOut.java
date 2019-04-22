package LIC.UC04v1.Scripts;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProdOut {
    public ProdOut(){
        System.out.println("###################");
        System.out.println("###    Prod     ###");
        System.out.println("###################");
    }
}