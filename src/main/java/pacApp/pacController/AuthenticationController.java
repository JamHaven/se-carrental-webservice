package pacApp.pacController;

import org.apache.commons.validator.routines.EmailValidator;
import pacApp.pacData.UserRepository;
import pacApp.pacException.AuthenticationForbiddenException;
import pacApp.pacModel.User;
import pacApp.pacModel.response.JwtTokenResponse;
import pacApp.pacSecurity.JwtAuthenticationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final UserRepository repository;
    private JwtAuthenticationService authenticationService;

    public AuthenticationController(UserRepository repository, JwtAuthenticationService authenticationService){
        this.repository = repository;
        this.authenticationService = authenticationService;
    }

    @CrossOrigin
    @GetMapping("/auth")
    public List<User> getAllUsers(){
        return this.repository.findAll();
    }

    @CrossOrigin
    @RequestMapping(value = "/auth", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtTokenResponse> authenticateUser(@RequestBody User user){
        log.info("User authentication: " + user.toString());

        if (user.getEmail() == null || user.getPassword() == null) {
            throw new AuthenticationForbiddenException();
        }

        EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(user.getEmail())) {
            throw new AuthenticationForbiddenException();
        }

        Optional<User> optUser = this.repository.findOneByEmail(user.getEmail());
        optUser.orElseThrow(() -> new AuthenticationForbiddenException());

        User savedUser = optUser.get();
        log.info("User: " + savedUser.toString());

        return new ResponseEntity<>(authenticationService.generateJwtToken(savedUser.getEmail(), user.getPassword()), HttpStatus.OK);
    }

}
