package com.example.library.server.security;

import com.example.library.server.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LibraryReactiveUserDetailsService implements ReactiveUserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userService.findOneByEmail(username).map(LibraryUserDetails::new);
    }
}
