package HooYah.User.UserService;

import HooYah.User.user.dto.request.RegisterDto;
import HooYah.User.user.repository.UserRepository;
import HooYah.User.user.service.UserService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegisterTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    void test() {
        Assertions.assertDoesNotThrow(() -> System.out.println(userRepository.findByEmail("test@example.com").get()));
    }

    @Test
    @DisplayName("20개의 스레드가 동시에 같은 이메일로 가입 시도 시 1명만 성공해야 한다")
    void registerConcurrencyTest() throws InterruptedException {
        int threadCount = 20;
        String targetEmail = "test@example.com";
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(targetEmail);
        registerDto.setPassword("password");
        registerDto.setName("test");

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    userService.registerWithEmail(registerDto);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Assertions.assertDoesNotThrow(() -> System.out.println(userRepository.findByEmail(targetEmail).get()));
        // if contains duplicate email --> throw Query did not return a unique result: 3 results were returned
    }

}
