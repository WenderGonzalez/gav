package com.proyecto.gav.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        final String defaultRedirectUrl = "/login";
        String redirectUrl = determineRedirectUrl(authentication, defaultRedirectUrl);

        response.sendRedirect(redirectUrl);
    }

    private String determineRedirectUrl(Authentication authentication, String defaultUrl) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority auth : authorities) {
            switch (auth.getAuthority()) {
                case "ROLE_ADMIN":
                    return "/admin/inicio";
                case "ROLE_CONDUCTOR":
                    return "/conductor/inicio";
                case "ROLE_CLIENTE":
                    return "/cliente/inicio";
            }
        }
        return defaultUrl;
    }
}