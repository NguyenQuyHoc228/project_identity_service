package com.devnguyen.test_skill.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

// 1. tự động tạo get-set, constructor,..
@Data
// khai báo 2 annotation này thì có thể có 2 hàm constructor
@NoArgsConstructor
@AllArgsConstructor
// tự động tạo ra 1 buider class cho 1 dto
@Builder

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {

    @Size(min = 3, message = "USERNAME_INVALID")
     String username;

    @Size(min = 5, message = "PASSWORD_INVALID")
     String password;
     String firstName;
     String lastName;
     LocalDate dob;


}
