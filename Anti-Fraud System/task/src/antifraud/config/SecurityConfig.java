package antifraud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)                           // For modifying requests via Postman
                .headers(headers -> headers.frameOptions().disable())           // for Postman, the H2 console
                .authorizeHttpRequests(requests -> requests                     // manage access
                    .requestMatchers("/error", "/403", "/404", "/500").permitAll()

                    .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole("MERCHANT")
                    .requestMatchers(
                            new AntPathRequestMatcher("/api/auth/user/*", HttpMethod.DELETE.name()),
                            new AntPathRequestMatcher("/api/auth/access", HttpMethod.PUT.name()),
                            new AntPathRequestMatcher("/api/auth/role", HttpMethod.PUT.name())
                    ).hasRole("ADMINISTRATOR")
                    .requestMatchers(
                            new AntPathRequestMatcher("/api/antifraud/suspicious-ip/**"),
                            new AntPathRequestMatcher("/api/antifraud/stolencard/**"),
                            new AntPathRequestMatcher("/api/antifraud/history", HttpMethod.GET.name()),
                            new AntPathRequestMatcher("/api/antifraud/history/*", HttpMethod.GET.name()),
                            new AntPathRequestMatcher("/api/antifraud/transaction", HttpMethod.PUT.name())
                    ).hasRole("SUPPORT")
                    .requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")

                    .requestMatchers("/actuator/shutdown").permitAll()      // needed to run test
                    .anyRequest().denyAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // Did not work in securityFilterChain because H2 works aside from Spring Web
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }
}
