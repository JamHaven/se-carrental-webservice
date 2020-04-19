package pacApp.pacController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pacApp.pacData.UserRepository;
import pacApp.pacModel.Currency;
import pacApp.pacModel.User;
import pacApp.pacModel.request.Booking;
import pacApp.pacModel.request.UserInfo;
import pacApp.pacModel.response.GenericResponse;
import pacApp.pacSecurity.JwtAuthenticatedProfile;

import java.util.Optional;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticatedProfile)) {
            GenericResponse response = new GenericResponse(HttpStatus.FORBIDDEN.value(),"Authentication failure");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        JwtAuthenticatedProfile authenticatedProfile = (JwtAuthenticatedProfile) auth;
        String userEmail = authenticatedProfile.getName();

        Optional<User> optUser = this.repository.findOneByEmail(userEmail);

        if (!optUser.isPresent()){
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),"Invalid user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User user = optUser.get();

        UserInfo userInfo = this.convertUserToUserInfo(user);

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateUser(@RequestBody UserInfo userInfo) {
        if (userInfo == null) {
            GenericResponse response = new GenericResponse(400, "Missing request body");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticatedProfile)) {
            GenericResponse response = new GenericResponse(403,"Authentication failure");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        JwtAuthenticatedProfile authenticatedProfile = (JwtAuthenticatedProfile) auth;
        String userEmail = authenticatedProfile.getName();

        Optional<User> optUser = this.repository.findOneByEmail(userEmail);

        if (!optUser.isPresent()){
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),"Invalid user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User user = optUser.get();

        if (userInfo.getDefaultCurrency() != null) {
            String currency = userInfo.getDefaultCurrency();
            int id = Currency.getCurrencyId(currency);

            if (id == -1) {
                GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),currency + " is invalid");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Currency newDefaultCurrency = Currency.valueOf(currency);
            user.setDefaultCurrency(newDefaultCurrency);
        }

        this.repository.saveAndFlush(user);

        GenericResponse response = new GenericResponse(HttpStatus.OK.value(),"User settings updated");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    protected UserInfo convertUserToUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        Currency currency = user.getDefaultCurrency();
        userInfo.setDefaultCurrency(currency.name());

        return userInfo;
    }


}
