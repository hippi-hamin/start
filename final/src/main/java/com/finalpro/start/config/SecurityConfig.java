package com.finalpro.start.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.AllArgsConstructor;

@EnableWebSecurity
@AllArgsConstructor
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .requestMatchers("/admin/**").hasRole("ADMIN") // /admin 경로에 대한 접근 제한
                .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/upLoad/**").permitAll() // 정적 자원에 대한 접근 허용
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/signin") // 로그인 페이지 URL 설정
                .defaultSuccessUrl("/") // 로그인 성공 후 리다이렉트할 URL 설정
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout") // 로그아웃 URL 설정
                .logoutSuccessUrl("/signin") // 로그아웃 성공 후 리다이렉트할 URL 설정
                .invalidateHttpSession(true) // 세션 무효화 설정
                .permitAll(); // 로그아웃 페이지에 대한 접근 허용

        return http.build();
    }
}
