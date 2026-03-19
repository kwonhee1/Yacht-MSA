package HooYah.User.passwordencoder;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordEncoder implements PasswordEncoder {

    private final int salt;

    /*
        @Param : the salt to hash with (perhaps generated using BCrypt.gensalt)
     */
    public BCryptPasswordEncoder(
            int salt
    ) {
        this.salt = salt;
    }

    public BCryptPasswordEncoder() {
        this(10);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        }
        return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt(salt));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }

}
