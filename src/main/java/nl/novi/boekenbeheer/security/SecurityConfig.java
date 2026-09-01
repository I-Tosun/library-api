package nl.novi.boekenbeheer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/authors/**").hasAnyRole("BEHEERDER", "KLANT")
                        .requestMatchers(HttpMethod.POST, "/api/authors/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.PUT, "/api/authors/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/authors/**").hasRole("BEHEERDER")

                        .requestMatchers(HttpMethod.GET, "/api/books/**").hasAnyRole("BEHEERDER", "KLANT")
                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("BEHEERDER")

                        .requestMatchers(HttpMethod.GET, "/api/book-copies/**").hasAnyRole("BEHEERDER", "KLANT")
                        .requestMatchers(HttpMethod.POST, "/api/book-copies/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/book-copies/**").hasRole("BEHEERDER")

                        .requestMatchers("/api/customers/**").hasRole("BEHEERDER")
                        .requestMatchers("/api/library-cards/**").hasRole("BEHEERDER")

                        .requestMatchers(HttpMethod.GET, "/api/loans/**").hasAnyRole("BEHEERDER", "KLANT")
                        .requestMatchers(HttpMethod.POST, "/api/loans/**").hasAnyRole("BEHEERDER", "KLANT")
                        .requestMatchers(HttpMethod.PUT, "/api/loans/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/loans/**").hasRole("BEHEERDER")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(this::extractRoles);
        return jwtConverter;
    }

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return List.of();
        }
        Object rolesObject = realmAccess.get("roles");
        if (!(rolesObject instanceof List<?> roles)) {
            return List.of();
        }
        return roles.stream()
                .map(Object::toString)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}