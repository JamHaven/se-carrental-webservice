package pacApp.pacController;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.RegexValidator;
import pacApp.pacData.UserRepository;
import pacApp.pacException.RegistrationBadRequestException;
import pacApp.pacModel.User;
import pacApp.pacModel.response.GenericResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);
    private final UserRepository repository;

    public RegistrationController(UserRepository repository){
        this.repository = repository;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericResponse> registerUser(@RequestBody User user){
        log.info("User registration: " + user.toString());

        if (user.getEmail() == null || user.getPassword() == null) {
            throw new RegistrationBadRequestException();
        }

        EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(user.getEmail())) {
            throw new RegistrationBadRequestException();
        }

        Optional<User> optUser = this.repository.findOneByEmail(user.getEmail());
        if (optUser.isPresent()){
            GenericResponse response = new GenericResponse(409,"User already registered");
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }

        RegexValidator validator = new RegexValidator("((?=.*[a-z])(?=.*\\d)(?=.*[@#$%])(?=.*[A-Z]).{6,16})");
        if(!validator.isValid(user.getPassword())){
            throw new RegistrationBadRequestException();
        }

        this.repository.saveUser(user);

        GenericResponse response = new GenericResponse(200, "User registration successful");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
