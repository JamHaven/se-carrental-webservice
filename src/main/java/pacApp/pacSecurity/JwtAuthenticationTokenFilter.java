package pacApp.pacSecurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Value("${jwt.header}")
    private String tokenHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        this.authenticateToken(request, response);
        filterChain.doFilter(request, response);
    }

    protected void authenticateToken(HttpServletRequest request, HttpServletResponse response) {
        String authToken = null;
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            log.info("cookies is null");
            return;
        }

        for (Cookie cookie : cookies) {
            log.info(cookie.getName());
            if (cookie.getName().equals("token")) {
                authToken = cookie.getValue();
            }
        }

        if (authToken == null) {
            return;
        }

        JwtAuthentication authentication = new JwtAuthentication(authToken);
        this.updateSecurityContextAuthentication(authentication);
    }

    protected void updateSecurityContextAuthentication(Authentication authentication){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication auth = securityContext.getAuthentication();

        if (auth != null && auth instanceof JwtAuthenticatedProfile) {
            return;
        }

        if (auth == null){
            securityContext.setAuthentication(authentication);
        }
    }
}
