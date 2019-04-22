package LIC.UC04v1.services.security;

import org.springframework.stereotype.Service;

/**
 * Created by bingyang.wei on 5/9/2017.
 */
public interface EncryptionService {
    String encryptString(String input);
    boolean checkPassword(String plainPassword, String encrptPassword);
}
