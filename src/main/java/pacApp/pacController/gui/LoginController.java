package pacApp.pacController.gui;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pacApp.pacController.AuthenticationController;
import pacApp.pacModel.User;
import pacApp.pacModel.Session.CurrentAuthUser;
import pacApp.pacModel.response.JwtTokenResponse;

@RestController
@Scope("session")
public class LoginController {
	
	@Autowired
	AuthenticationController autController; 
	
	@Autowired
	CurrentAuthUser cau; 
	
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	@CrossOrigin
    @RequestMapping(value = "/login", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    //public ResponseEntity<JwtTokenResponse> authenticateUser(@RequestBody User user, HttpServletRequest request){
	public String authenticateUser(@RequestBody User user, HttpServletRequest request){		
		//TODO: check user
		cau.setSessionId(request.getSession().getId());
		cau.setUser(user);		
		if (user != null)
			return cau.getSessionId();
		autController.authenticateUser(user); 
		return null; 
    }
	
	@CrossOrigin
	@GetMapping("/login")
	public String loginTest(HttpServletRequest request) {		
		return request.getSession().getId();
	}
	@CrossOrigin
	@GetMapping("/currentUser")
	public String getSessionId(HttpServletRequest request) {		
		if (cau.getUser() == null)
			return "Session abgelaufen";
		return "Session: "+ cau.getSessionId() + " Email: " + cau.getUser().getEmail(); 
	}
}
