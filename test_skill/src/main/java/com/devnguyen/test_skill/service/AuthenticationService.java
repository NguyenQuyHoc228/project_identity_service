package com.devnguyen.test_skill.service;
import com.devnguyen.test_skill.dto.request.AuthenticationRequest;
import com.devnguyen.test_skill.dto.request.IntrospectRequest;
import com.devnguyen.test_skill.dto.response.AuthenticationResponse;
import com.devnguyen.test_skill.dto.response.IntrospectResponse;
import com.devnguyen.test_skill.exception.AppException;
import com.devnguyen.test_skill.exception.ErrorCode;
import com.devnguyen.test_skill.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor

@Service
public class AuthenticationService {

    UserRepository userRepository;

    @NonFinal
    @Value(("${jwt.signerKey}"))
    protected  String SIGN_KEY;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }




    public AuthenticationResponse authenticate(AuthenticationRequest request){

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow( () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticate =  passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticate){
            throw new AppException(ErrorCode.UNAUTHENTICATION);
        }

        var token = generateToken(request.getUsername());

        return AuthenticationResponse.builder()
                .token(token)
                .authenticate(true)
                .build();

    }

    // khoi tao token
    private String generateToken(String username){

        // tao header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // build body claim cho payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("devNguyen.com")
                .issueTime(new Date())
                .expirationTime( new Date( Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()) )
                .claim("customClaim", "custom")
                .build();

        // payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());



        // tu header vaf payload -> khoi tao JWSObject
        JWSObject jwsObject = new JWSObject(header,payload);

        // ki token
        try {
            jwsObject.sign(new MACSigner(SIGN_KEY));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cant not create token", e);
            throw new RuntimeException(e);
        }
    }
}
