package HooYah.User.user.service;

import HooYah.User.passwordencoder.PasswordEncoder;
import HooYah.User.user.domain.User;
import HooYah.User.user.dto.request.LoginDto;
import HooYah.User.user.dto.request.RegisterDto;
import HooYah.User.user.repository.UserRepository;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final TransactionTemplate transactionTemplate;

    public User registerWithEmail(RegisterDto dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User createdUser = transactionTemplate.execute((status)->{
            if(userRepository.findByEmailWithLock(dto.getEmail()).isPresent())
                throw new CustomException(ErrorCode.CONFLICT);

            User user = dto.toEntity(encodedPassword);
            return userRepository.save(user);
        });

        return createdUser;
    }

    public User login(LoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        user.login(dto.getEmail(), dto.getPassword(), passwordEncoder);

        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND));
    }

    @Transactional
    public void deleteUser(Long userId) {
        // todo : delete 구현
    }

    public List<User> getUserList(List<Long> userIdList) {
        return userRepository.findAllById(userIdList);
    }

}
