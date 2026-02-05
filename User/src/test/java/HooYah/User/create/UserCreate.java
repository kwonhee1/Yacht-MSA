package HooYah.User.create;

import HooYah.User.passwordencoder.PasswordEncoder;
import HooYah.User.user.domain.User;
import HooYah.User.user.dto.request.RegisterDto;
import HooYah.User.user.repository.UserRepository;
import HooYah.User.user.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@Disabled
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://222.237.95.115:8081/msa",
        "spring.datasource.username=user",
        "spring.datasource.password=password"
})
public class UserCreate {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    public void createUser100() {
        Long max = 100L; // 100개의 email의 user를 만든다 (email1 ~ email100)
        for(Long i = 1L; i <= max; i++)
            createUser(i);

        Assertions.assertTrue(true);
    }

    private void createUser(Long number) {
        String email = String.format("%s%d", "email", number);

        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent())
            return;

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setPassword("password");
        registerDto.setName("name");

        userService.registerWithEmail(registerDto);
    }

}
