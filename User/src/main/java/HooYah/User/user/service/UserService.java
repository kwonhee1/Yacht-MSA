package HooYah.User.user.service;

import HooYah.User.common.excetion.CustomException;
import HooYah.User.common.excetion.ErrorCode;
import HooYah.User.user.domain.User;
import HooYah.User.user.dto.request.LoginDto;
import HooYah.User.user.dto.request.RegisterDto;
import HooYah.User.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerWithEmail(RegisterDto dto) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new CustomException(ErrorCode.CONFLICT);

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = dto.toEntity(encodedPassword);
        return userRepository.save(user);
    }

    @Transactional
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

}
