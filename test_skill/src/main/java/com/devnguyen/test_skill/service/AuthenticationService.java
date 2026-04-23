package com.devnguyen.test_skill.service;
import com.devnguyen.test_skill.dto.request.AuthenticationRequest;
import com.devnguyen.test_skill.dto.request.IntrospectRequest;
import com.devnguyen.test_skill.dto.request.LogoutRequest;
import com.devnguyen.test_skill.dto.request.RefreshRequest;
import com.devnguyen.test_skill.dto.response.AuthenticationResponse;
import com.devnguyen.test_skill.dto.response.IntrospectResponse;
import com.devnguyen.test_skill.exception.AppException;
import com.devnguyen.test_skill.exception.ErrorCode;
import com.devnguyen.test_skill.repository.InvalidatedTokenRepository;
import com.devnguyen.test_skill.repository.UserRepository;
import com.devnguyen.test_skill.user.InvalidatedToken;
import com.devnguyen.test_skill.user.User;
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
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor

@Service
public class AuthenticationService {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value(("${jwt.signerKey}"))
    protected  String SIGN_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        boolean isValid = true;

        try {
            verifyToken(token, false);
        }catch (AppException e){
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }




    public AuthenticationResponse authenticate(AuthenticationRequest request){

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow( () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticate =  passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticate){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticate(true)
                .build();

    }

    // khoi tao ham logout token
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Mã thông báo đã hết hạn");
        }
    }



    // khoi toa ham refresh token
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder()
                        .id(jit)
                        .expiryTime(expiryTime)
                        .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticate(true)
                .build();
    }

    // khoi tao ham SignedJWT verifyToken()
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if ( !(verified && expiryTime.after( new Date() ) ) ){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }


        return signedJWT;
    }


    // khoi tao token
    private String generateToken(User user){

        // tao header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // build body claim cho payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("devNguyen.com")
                .issueTime(new Date())
                .expirationTime( new Date( Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()) )

                // xử lý logout token
                .jwtID(UUID.randomUUID().toString())

                .claim("scope", buildScope(user))
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

    // build scope tu 1 User
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())){
                    role.getPermissions()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
                }
            });
        }

        return stringJoiner.toString();
    }

}
