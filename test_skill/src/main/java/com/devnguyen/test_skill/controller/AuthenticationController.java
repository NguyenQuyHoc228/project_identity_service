package com.devnguyen.test_skill.controller;
import com.devnguyen.test_skill.dto.request.AuthenticationRequest;
import com.devnguyen.test_skill.dto.request.IntrospectRequest;
import com.devnguyen.test_skill.dto.response.ApiResponse;
import com.devnguyen.test_skill.dto.response.AuthenticationResponse;
import com.devnguyen.test_skill.dto.response.IntrospectResponse;
import com.devnguyen.test_skill.service.AuthenticationService;
import com.devnguyen.test_skill.service.UserService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationController {
     AuthenticationService authenticationService;

     @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
         var result = authenticationService.authenticate(request);

         return ApiResponse.<AuthenticationResponse>builder()
                 .result(result)
                 .build();
     }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

}
