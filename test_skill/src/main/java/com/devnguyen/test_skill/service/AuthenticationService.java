package com.devnguyen.test_skill.service;
import com.devnguyen.test_skill.dto.request.AuthenticationRequest;
import com.devnguyen.test_skill.exception.AppException;
import com.devnguyen.test_skill.exception.ErrorCode;
import com.devnguyen.test_skill.repository.UserRepository;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor

@Service
public class AuthenticationService {

    UserRepository userRepository;

    public boolean authenticate(AuthenticationRequest request){

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow( () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        return passwordEncoder.matches(request.getPassword(), user.getPassword());
    }
}
