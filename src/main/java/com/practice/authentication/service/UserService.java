package com.practice.authentication.service;

import com.practice.authentication.config.*;
import com.practice.authentication.entity.UserEntity;
import com.practice.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    DemoUserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenGenerator jwtTokenGenerator;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserEntity createUser(UserEntity user) {
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        Map<String,Object> claims = new HashMap<>();
        String token = jwtTokenGenerator.generateJwtToken(claims,userDetails.getUsername());

        return new AuthenticationResponse(token,jwtTokenGenerator.getIssueTimeFromToken(token),jwtTokenGenerator.getExpiryTimeFromToken(token));
    }

    void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
