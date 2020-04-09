package pacApp.pacSecurity;

import pacApp.pacData.UserRepository;
import pacApp.pacException.AuthenticationForbiddenException;
import pacApp.pacModel.User;
import pacApp.pacModel.response.JwtTokenResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtAuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationService.class);
    private UserRepository userRepository;
    private JwtTokenAuthorizationService tokenService;
    private PasswordEncoder passwordEncoder;

    public JwtAuthenticationService(UserRepository userRepository, JwtTokenAuthorizationService tokenService, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public JwtTokenResponse generateJwtToken(String email, String password){
        log.info("generateJwtToken");
        log.info(password);
        return this.userRepository.findOneByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> new JwtTokenResponse(tokenService.generateToken(email)))
                .orElseThrow(() -> new AuthenticationForbiddenException());
    }
}
