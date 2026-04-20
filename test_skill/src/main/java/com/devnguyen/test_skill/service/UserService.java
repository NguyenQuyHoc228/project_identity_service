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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Slf4j
@Service
@RequiredArgsConstructor // tạo 1 cái constructor cho tất cả các biến được định nghĩa là final ( thay thế @Autowrite)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // bất cứ field nào không khai báo kdl, mặc định là private
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    // post user
    public UserResponse createUser(UserCreateRequest request){
        // exception Existed
        if (userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED); // Exception
        }
        // map dữ liệu request về obj User
        User user = userMapper.toUser(request);

        // security - mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Thêm 1 field role mặc định là User.
        Set<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRoles(roles);

        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    // get listUser
    // để User vào được hàm này cần thỏa mãn điều kiện của Annotation này.
    // chỉ có role Admin mới có quyền lấy danh sách người dùng.
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers(){
        log.info("Message: In method get user");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    // get userId
    // khi method này được thực hiện xong thì mới kiểm tra điều kiện của @Annotation này.
    // User chỉ có thể lấy được chính thông tin của chính mình
    // returnObject = chính là method UserResponse - authentication là User đang đăng nhập
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String userId){
        log.info("Message: In method get user by Id");
        return userMapper.toUserResponse( userRepository.findById(userId)
                .orElseThrow( () -> new RuntimeException(" Không tìm thấy User !! ") ) ); // RuntimeException
    }

    public UserResponse getMyInfo(){
        // khi 1 request xác thực thành công thì sẽ được lưu vào 1 SecurityContextHolder
        //.getContext() -> User hiện tại
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(
                () ->new AppException(ErrorCode.USER_NOT_EXISTED) );

        return userMapper.toUserResponse(user);
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
