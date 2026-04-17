package com.devnguyen.test_skill.service;

import com.devnguyen.test_skill.dto.request.UserCreateRequest;
import com.devnguyen.test_skill.dto.request.UserUpdateRequest;
import com.devnguyen.test_skill.dto.response.UserResponse;
import com.devnguyen.test_skill.enums.Role;
import com.devnguyen.test_skill.exception.AppException;
import com.devnguyen.test_skill.exception.ErrorCode;
import com.devnguyen.test_skill.mapper.UserMapper;
import com.devnguyen.test_skill.repository.UserRepository;
import com.devnguyen.test_skill.user.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor // tạo 1 cái constructor cho tất cả các biến được định nghĩa là final ( thay thế @Autowrite)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // bất cứ field nào không khai báo kdl, mặc định là private
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;

    // post user
    public User createUser(UserCreateRequest request){
        // exception Existed
        if (userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED); // Exception
        }
        // map dữ liệu request về obj User
        User user = userMapper.toUser(request);

        // security - mã hóa mật khẩu
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Thêm 1 field role mặc định là User.
        // === THÊM ROLE MẶC ĐỊNH ===
        Set<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRoles(roles);

        return userRepository.save(user);
    }

    // get listUser
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    // get userId
    public UserResponse getUser(String userId){
        return userMapper.toUserResponse( userRepository.findById(userId)
                .orElseThrow( () -> new RuntimeException(" Không tìm thấy User !! ") ) ); // RuntimeException
    }

    // put userId
    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new RuntimeException(" Không tìm thấy User !! ") );

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse( userRepository.save(user) );
    }


    //delete userId
    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }


}
