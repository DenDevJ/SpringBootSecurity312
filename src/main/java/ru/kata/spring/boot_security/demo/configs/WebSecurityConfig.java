package ru.kata.spring.boot_security.demo.configs;

import ru.kata.spring.boot_security.demo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserService userService;
    private final SuccessUserHandler successUserHandler;

    public WebSecurityConfig(@Lazy UserService userService, SuccessUserHandler successUserHandler) {
        this.userService = userService;
        this.successUserHandler = successUserHandler;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService((UserDetailsService) userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                // Общедоступные страницы и ресурсы
                .antMatchers("/", "/login", "/logout", "/index").permitAll()
                // Фавиконы
                .antMatchers("/favicon.ico", "/favicon-32x32.png", "/favicon-16x16.png").permitAll()
                // Страницы для ADMIN (полный доступ)
                .antMatchers("/admin/**", "/admin/addUser", "/admin/edit/**", "/admin/delete/**").hasRole("ADMIN")
                // Страница для USER
                .antMatchers("/user").hasAnyRole("USER", "ADMIN")
                // Все остальные запросы требуют аутентификации
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(successUserHandler)
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout")
                .permitAll();
        return http.build();
    }

    // Полное игнорирование статических ресурсов Spring Security
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/static/**",
                "/css/**",
                "/js/**",
                "/images/**",
                "/favicon.ico",
                "/favicon-32x32.png",
                "/favicon-16x16.png"
        );
    }
}