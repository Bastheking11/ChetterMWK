package domain.utility.authentication;

import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import java.security.Key;

@Stateless
public class KeyGenerator {

    public static Key generate(String secret) {
        return new SecretKeySpec(secret.getBytes(), 0, secret.getBytes().length, "DES");
    }

}
