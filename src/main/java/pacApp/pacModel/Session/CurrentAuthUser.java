package pacApp.pacModel.Session;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pacApp.pacModel.User;

@Component
@Scope("session")
public class CurrentAuthUser {
	
	private String sessionId;
	private String timestamp; 
	private User user;
	
	public CurrentAuthUser() {
		this.timestamp = java.time.LocalTime.now().toString();
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public boolean isSessionValid() {
		return user != null? true :false; 
	}
	
}
