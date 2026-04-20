package com.devnguyen.test_skill.controller;

import com.devnguyen.test_skill.dto.response.ApiResponse;
import com.devnguyen.test_skill.dto.request.UserCreateRequest;
import com.devnguyen.test_skill.dto.request.UserUpdateRequest;
import com.devnguyen.test_skill.dto.response.UserResponse;
import com.devnguyen.test_skill.service.UserService;
import com.devnguyen.test_skill.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // post user
    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }


    // get listUser
    @GetMapping
    List<User> getUsers(){
        return userService.getUsers();
    }


    // get UserId
    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") String userId){

        return userService.getUser(userId);
    }


    //put userId
    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return userService.updateUser(userId, request);
    }


    //delete userId
    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return " Xóa thành công User ^^ ";
    }



}
