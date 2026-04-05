package com.iimj.resultportal.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("Admin@123"))   // same hardcoded password as before
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);    // in-memory user store[web:28][web:39]
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();              // stores password securely[web:30][web:39]
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // For a quick integration you can disable CSRF;
            // for production, configure CSRF tokens instead of disabling.
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // public endpoints (adjust to match your actual mappings)
                .requestMatchers("/", "/index", "/captcha", "/js/**", "/css/**", "/images/**").permitAll()
                // admin area
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )

            .formLogin(form -> form
                .loginPage("/admin/login")               // show your custom login page[web:31][web:39][web:41]
                .loginProcessingUrl("/admin/login")      // Spring Security handles POST here (no controller)[web:29][web:35]
                .defaultSuccessUrl("/admin/dashboard", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
