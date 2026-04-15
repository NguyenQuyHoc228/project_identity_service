package com.devnguyen.test_skill.controller;
import com.devnguyen.test_skill.dto.request.AuthenticationRequest;
import com.devnguyen.test_skill.dto.response.ApiResponse;
import com.devnguyen.test_skill.dto.response.AuthenticationResponse;
import com.devnguyen.test_skill.service.AuthenticationService;
import com.devnguyen.test_skill.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationController {
     AuthenticationService authenticationService;

     @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
         var result = authenticationService.authenticate(request);

         return ApiResponse.<AuthenticationResponse>builder()
                 .result(result)
                 .build();
     }


}
