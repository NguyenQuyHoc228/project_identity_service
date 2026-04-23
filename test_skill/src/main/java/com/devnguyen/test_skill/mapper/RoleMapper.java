package com.devnguyen.test_skill.mapper;

import com.devnguyen.test_skill.dto.request.RoleRequest;
import com.devnguyen.test_skill.dto.response.RoleResponse;
import com.devnguyen.test_skill.user.Permission;
import com.devnguyen.test_skill.user.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse (Role role);

    default String map(Permission permission) {
        return permission.getName();
    }
}
