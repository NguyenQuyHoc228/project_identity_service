package com.devnguyen.test_skill.mapper;

import com.devnguyen.test_skill.dto.request.PermissionRequest;
import com.devnguyen.test_skill.dto.response.PermissionResponse;
import com.devnguyen.test_skill.user.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

}
