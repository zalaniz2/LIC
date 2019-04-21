package LIC.UC04v1.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by bingyang.wei on 5/9/2017.
 */
@Service
public class EncryptionServiceImpl implements EncryptionService{

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public void setbCryptPasswordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String encryptString(String input) {
        return bCryptPasswordEncoder.encode(input);
    }

    @Override
    public boolean checkPassword(String plainPassword, String encrptPassword) {
        return bCryptPasswordEncoder.matches(plainPassword, encrptPassword);
    }
}