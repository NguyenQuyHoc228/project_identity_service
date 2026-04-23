package com.devnguyen.test_skill.mapper;

import com.devnguyen.test_skill.dto.request.UserCreateRequest;
import com.devnguyen.test_skill.dto.request.UserUpdateRequest;
import com.devnguyen.test_skill.dto.response.UserResponse;
import com.devnguyen.test_skill.user.Role;
import com.devnguyen.test_skill.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // nhận 1 cái parameter là request có kiểu là UserCreateRequest -> và trả về Class là User
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);

    // map data cua request vao obj user (update user)
    @Mapping(target = "roles",ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    // 👇 thêm cái này
    default String map(Role role) {
        return role.getName();
    }
}
