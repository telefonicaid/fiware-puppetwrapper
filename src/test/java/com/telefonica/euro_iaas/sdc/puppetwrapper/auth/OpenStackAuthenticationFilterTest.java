package com.telefonica.euro_iaas.sdc.puppetwrapper.auth;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.AuthenticationEntryPoint;

public class OpenStackAuthenticationFilterTest {

    private OpenStackAuthenticationFilter openStackAuthenticationFilter;

    private AuthenticationManager authenticationManager;
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Before
    public void setup() {
        authenticationManager = mock(AuthenticationManager.class);
        authenticationEntryPoint = mock(AuthenticationEntryPoint.class);

        openStackAuthenticationFilter = new OpenStackAuthenticationFilter();
        openStackAuthenticationFilter.setAuthenticationManager(authenticationManager);
        openStackAuthenticationFilter.setAuthenticationEntryPoint(authenticationEntryPoint);
    }

    @Test
    public void doFilterTestRootPath() throws IOException, ServletException {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        HttpSession httpSession = mock(HttpSession.class);

        when(servletRequest.getHeader(anyString())).thenReturn("3df25213cac246f8bccad5c70cb3582e");
        when(servletRequest.getPathInfo()).thenReturn("/");
        when(servletRequest.getSession()).thenReturn(httpSession);
        when(httpSession.getId()).thenReturn("1234");

        openStackAuthenticationFilter.doFilter(servletRequest, servletResponse, filterChain);
    }

    @Test
    public void doFilterTestAnyPath() throws IOException, ServletException {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        HttpSession httpSession = mock(HttpSession.class);
        Authentication authResult = mock(Authentication.class);
        PaasManagerUser paasUser=mock(PaasManagerUser.class);
        

        when(servletRequest.getHeader(anyString())).thenReturn("3df25213cac246f8bccad5c70cb3582e")
                .thenReturn("00000000000000000000000000000194").thenReturn("1234");
        when(servletRequest.getPathInfo()).thenReturn("/path");
        when(servletRequest.getSession()).thenReturn(httpSession);
        when(httpSession.getId()).thenReturn("1234");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authResult);
        
        when(authResult.getPrincipal()).thenReturn(paasUser);

        openStackAuthenticationFilter.doFilter(servletRequest, servletResponse, filterChain);
    }
}
