package org.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider provider) {
        return provider::authenticate;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfRepo.setCookiePath("/");
        csrfRepo.setCookieName("XSRF-TOKEN");
        csrfRepo.setHeaderName("X-XSRF-TOKEN");

        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();
        csrfHandler.setCsrfRequestAttributeName(null);

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfRepo)
                        .csrfTokenRequestHandler(csrfHandler)
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/h2-console/**"),
                                new AntPathRequestMatcher("/api/**")
                        )
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> {})
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .headers(headers -> headers.frameOptions(fo -> fo.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/city-objects/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/work-crews/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/work-orders/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/city-reports/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/city-objects/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/city-objects/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/city-objects/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/city-reports/**").hasAnyRole("USER", "DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/city-reports/*/reject").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/city-reports/**").hasAnyRole("USER", "DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/city-reports/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/work-crews/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/work-crews/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/work-crews/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/work-orders/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/work-orders/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/work-orders/**").hasRole("ADMIN")

                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}