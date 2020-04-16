package pacApp.pacController;

import org.apache.commons.validator.routines.EmailValidator;
import pacApp.pacData.UserRepository;
import pacApp.pacException.AuthenticationForbiddenException;
import pacApp.pacModel.User;
import pacApp.pacModel.response.GenericResponse;
import pacApp.pacModel.response.JwtTokenResponse;
import pacApp.pacSecurity.JwtAuthenticationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public ResponseEntity authenticateUser(HttpServletResponse response, @RequestBody User user){
        log.info("authenticateUser: " + user.toString());

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

        String token = authenticationService.generateJwtToken(savedUser.getEmail(), user.getPassword());

        log.info("token: " + token);

        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true);

        response.addCookie(tokenCookie);
        GenericResponse genericResponse = new GenericResponse(HttpStatus.OK.value(), "Authentication successful");

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity logoutUser(HttpServletRequest request, HttpServletResponse response) {
        log.info("logoutUser");

        String authToken = null;
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            GenericResponse responseBody = new GenericResponse(HttpStatus.BAD_REQUEST.value(), "Token not found");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                log.info("token: " + cookie.getValue());
                authToken = cookie.getValue();
                cookie.setValue("");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        if (authToken == null) {
            GenericResponse responseBody = new GenericResponse(HttpStatus.BAD_REQUEST.value(), "Token not found");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        GenericResponse responseBody = new GenericResponse(HttpStatus.OK.value(), "Logout successful");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

}
