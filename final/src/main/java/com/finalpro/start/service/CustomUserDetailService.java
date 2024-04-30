package com.finalpro.start.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
	

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin@admin".equals(username)) {
            // admin@admin 사용자의 정보 반환
            return User.withUsername("admin@admin")
                    .password("$2a$10$cup3jQKG7zrNE4shPaT08uoWELX91sXoD3kmrv0bQaW6aTaZ.Sep.") // BCrypt로 암호화된 비밀번호: admin
                    .roles("ADMIN") // ADMIN 역할 부여
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}

